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
import com.juyeon.androidpractice.data.db.entity.Follow
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
    private val followDao = db.followDao()

    private val _postId = MutableStateFlow(-1)

    var post by mutableStateOf<Post?>(null)
        private set
    var isLiked by mutableStateOf(false)
        private set
    var isScrapped by mutableStateOf(false)
        private set
    var isFollowing by mutableStateOf(false)
        private set
    var likeCount by mutableStateOf(0)
        private set
    var followerCount by mutableStateOf(0)
        private set
    // 현재 유저가 팔로우 중인 userId Set (댓글 팔로우 버튼에도 사용)
    var followingUserIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    private var _currentUserId = -1

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
        _currentUserId = currentUserId
        _postId.value = postId
        viewModelScope.launch {
            post = postDao.getPostById(postId)
            isLiked = likeDao.isLiked(currentUserId, postId)
            isScrapped = scrapDao.isScrapped(currentUserId, postId)
            post?.let { p ->
                if (p.authorId != currentUserId) {
                    isFollowing = followDao.isFollowing(currentUserId, p.authorId)
                }
            }
        }
        likeDao.getLikeCount(postId)
            .onEach { likeCount = it }
            .launchIn(viewModelScope)
        // 팔로잉 목록 실시간 구독 (댓글 팔로우 버튼 상태 반영)
        followDao.getFollowingIds(currentUserId)
            .onEach { followingUserIds = it.toSet() }
            .launchIn(viewModelScope)
        viewModelScope.launch {
            post?.let { p ->
                followDao.getFollowerCount(p.authorId)
                    .collect { followerCount = it }
            }
        }
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

    // 게시글 작성자 팔로우 (PostDetail 상단 버튼)
    fun toggleFollow(currentUserId: Int, currentNickname: String) {
        val p = post ?: return
        toggleFollowUser(targetUserId = p.authorId, currentUserId = currentUserId, currentNickname = currentNickname,
            onStateChange = { isFollowing = it })
    }

    // 범용 팔로우 (댓글 작성자 등)
    fun toggleFollowUser(targetUserId: Int, currentUserId: Int, currentNickname: String, onStateChange: ((Boolean) -> Unit)? = null) {
        if (targetUserId == currentUserId) return
        val alreadyFollowing = followingUserIds.contains(targetUserId)
        viewModelScope.launch {
            if (alreadyFollowing) {
                followDao.unfollow(currentUserId, targetUserId)
            } else {
                followDao.follow(Follow(currentUserId, targetUserId))
                val n = now()
                notificationDao.insert(
                    AppNotification(
                        targetUserId = targetUserId,
                        type = "FOLLOW",
                        fromUserId = currentUserId,
                        fromNickname = currentNickname,
                        message = "$currentNickname 님이 회원님을 팔로우하기 시작했습니다.",
                        createdAt = n
                    )
                )
                NotificationHelper.send(
                    context = getApplication(),
                    title = "새 팔로워",
                    body = "$currentNickname 님이 팔로우했습니다.",
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                )
            }
            onStateChange?.invoke(!alreadyFollowing)
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
                    val n = now()
                    notificationDao.insert(
                        AppNotification(
                            targetUserId = p.authorId,
                            type = "LIKE",
                            fromUserId = currentUserId,
                            fromNickname = currentNickname,
                            postId = postId,
                            postTitle = p.title,
                            message = "$currentNickname 님이 회원님의 게시글을 좋아합니다.",
                            createdAt = n
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

            // 게시글 작성자에게 댓글 알림
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

            // 대댓글이면 부모 댓글 작성자에게도 알림
            val parent = replyToComment
            if (parent != null && parent.authorId != authorId) {
                notificationDao.insert(
                    AppNotification(
                        targetUserId = parent.authorId,
                        type = "COMMENT",
                        fromUserId = authorId,
                        fromNickname = authorNickname,
                        postId = _postId.value,
                        postTitle = p?.title ?: "",
                        message = "$authorNickname 님이 회원님의 댓글에 답글을 남겼습니다: $text",
                        createdAt = n
                    )
                )
                NotificationHelper.send(
                    context = getApplication(),
                    title = "새 답글",
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
