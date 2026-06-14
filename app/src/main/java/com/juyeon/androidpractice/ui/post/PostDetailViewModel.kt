package com.juyeon.androidpractice.ui.post

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.AppNotification
import com.juyeon.androidpractice.data.db.entity.Comment
import com.juyeon.androidpractice.data.db.entity.CommentLike
import com.juyeon.androidpractice.data.db.entity.Like
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.Scrap
import com.juyeon.androidpractice.data.notification.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val postDao = db.postDao()
    private val commentDao = db.commentDao()
    private val likeDao = db.likeDao()
    private val scrapDao = db.scrapDao()
    private val commentLikeDao = db.commentLikeDao()
    private val notificationDao = db.notificationDao()

    private val _postId = MutableStateFlow(-1)

    var post by mutableStateOf<Post?>(null)
        private set
    var isLiked by mutableStateOf(false)
        private set
    var isScrapped by mutableStateOf(false)
        private set
    var likeCount by mutableStateOf(0)
        private set

    val comments: StateFlow<List<Comment>> = _postId
        .filter { it != -1 }
        .flatMapLatest { commentDao.getTopLevelComments(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val replies: StateFlow<Map<Int, List<Comment>>> = comments
        .flatMapLatest { topLevel ->
            if (topLevel.isEmpty()) flowOf(emptyMap())
            else combine(topLevel.map { c -> commentDao.getReplies(c.id).map { c.id to it } }) {
                it.toMap()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    var commentLikeCounts by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set
    var commentLikedByMe by mutableStateOf<Map<Int, Boolean>>(emptyMap())
        private set
    var commentInput by mutableStateOf("")
        private set
    var replyToComment by mutableStateOf<Comment?>(null)
        private set

    fun init(postId: Int, currentUserId: Int) {
        if (_postId.value == postId) return
        _postId.value = postId
        viewModelScope.launch {
            post = postDao.getPostById(postId)
            isLiked = likeDao.isLiked(currentUserId, postId)
            isScrapped = scrapDao.isScrapped(currentUserId, postId)
        }
        likeDao.getLikeCount(postId)
            .onEach { likeCount = it }
            .launchIn(viewModelScope)
    }

    fun refreshCommentInteractions(commentIds: List<Int>, currentUserId: Int) {
        viewModelScope.launch {
            val counts = commentIds.associateWith { id ->
                commentLikeDao.getLikeCount(id).first()
            }
            val liked = commentIds.associateWith { id ->
                commentLikeDao.isLiked(currentUserId, id)
            }
            commentLikeCounts = counts
            commentLikedByMe = liked
        }
    }

    fun toggleLike(currentUserId: Int, currentNickname: String) {
        val postId = _postId.value
        viewModelScope.launch {
            if (isLiked) {
                likeDao.delete(currentUserId, postId)
            } else {
                likeDao.insert(Like(currentUserId, postId))
                val p = post
                if (p != null && p.authorId != currentUserId) {
                    val now = now()
                    notificationDao.insert(
                        AppNotification(
                            targetUserId = p.authorId,
                            type = "LIKE",
                            fromUserId = currentUserId,
                            fromNickname = currentNickname,
                            postId = postId,
                            postTitle = p.title,
                            message = "$currentNickname 님이 회원님의 게시글을 좋아합니다.",
                            createdAt = now
                        )
                    )
                    NotificationHelper.send(
                        context = getApplication(),
                        title = "좋아요",
                        body = "$currentNickname 님이 회원님의 게시글을 좋아합니다.",
                        id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                    )
                }
            }
            isLiked = !isLiked
        }
    }

    fun toggleScrap(currentUserId: Int) {
        val postId = _postId.value
        viewModelScope.launch {
            if (isScrapped) scrapDao.delete(currentUserId, postId)
            else scrapDao.insert(Scrap(currentUserId, postId))
            isScrapped = !isScrapped
        }
    }

    fun toggleCommentLike(commentId: Int, currentUserId: Int) {
        viewModelScope.launch {
            val liked = commentLikedByMe[commentId] == true
            if (liked) commentLikeDao.delete(currentUserId, commentId)
            else commentLikeDao.insert(CommentLike(currentUserId, commentId))
            commentLikedByMe = commentLikedByMe + (commentId to !liked)
            val newCount = commentLikeDao.getLikeCount(commentId).first()
            commentLikeCounts = commentLikeCounts + (commentId to newCount)
        }
    }

    fun onCommentInputChange(value: String) { commentInput = value }

    fun setReplyTo(comment: Comment?) { replyToComment = comment; commentInput = "" }

    fun submitComment(authorId: Int, authorNickname: String) {
        val text = commentInput.trim()
        if (text.isBlank()) return
        val n = now()
        viewModelScope.launch {
            commentDao.insert(
                Comment(
                    postId = _postId.value,
                    authorId = authorId,
                    authorNickname = authorNickname,
                    body = text,
                    createdAt = n,
                    parentCommentId = replyToComment?.id,
                )
            )
            val p = post
            if (p != null && p.authorId != authorId) {
                notificationDao.insert(
                    AppNotification(
                        targetUserId = p.authorId,
                        type = "COMMENT",
                        fromUserId = authorId,
                        fromNickname = authorNickname,
                        postId = _postId.value,
                        postTitle = p.title,
                        message = "$authorNickname 님이 댓글을 남겼습니다: $text",
                        createdAt = n
                    )
                )
                NotificationHelper.send(
                    context = getApplication(),
                    title = "새 댓글",
                    body = "$authorNickname: $text",
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                )
            }
            commentInput = ""
            replyToComment = null
        }
    }

    private fun now() = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}
