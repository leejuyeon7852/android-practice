package com.juyeon.androidpractice.ui.post

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.AppNotification
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.CommentRequest
import com.juyeon.androidpractice.data.network.dto.CommentResponse
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.data.notification.NotificationHelper
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient.api
    private val notificationDao = AppDatabase.getInstance(application).notificationDao()

    var post by mutableStateOf<PostResponse?>(null)
        private set
    var comments by mutableStateOf<List<CommentResponse>>(emptyList())
        private set
    var isLiked by mutableStateOf(false)
        private set
    var likeCount by mutableStateOf(0)
        private set
    var isScrapped by mutableStateOf(false)
        private set
    var isFollowing by mutableStateOf(false)
        private set
    var commentLikeCounts by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set
    var commentLikedByMe by mutableStateOf<Map<Int, Boolean>>(emptyMap())
        private set
    var commentInput by mutableStateOf("")
        private set
    var replyToComment by mutableStateOf<CommentResponse?>(null)
        private set

    private var currentPostId = -1
    private var currentUserId = -1
    private var currentNickname = ""

    fun init(postId: Int, userId: Int, nickname: String) {
        currentPostId = postId
        currentUserId = userId
        currentNickname = nickname
        viewModelScope.launch {
            try {
                post = api.getPost(postId)
                val likeStatus = api.getLikeStatus(postId)
                isLiked = likeStatus.liked
                likeCount = likeStatus.likeCount
                val scrapStatus = api.getScrapStatus(postId)
                isScrapped = scrapStatus.scraped
                post?.let { p ->
                    if (p.authorId != userId) {
                        isFollowing = api.getFollowStatus(p.authorId).followed
                    }
                }
                loadComments()
            } catch (_: Exception) {}
        }
    }

    private suspend fun loadComments() {
        try {
            val loaded = api.getComments(currentPostId)
            comments = loaded
            val allIds = loaded.flatMap { listOf(it.id) + it.replies.map { r -> r.id } }
            commentLikeCounts = allIds.associateWith { 0 }
            commentLikedByMe = allIds.associateWith { false }
        } catch (_: Exception) {}
    }

    fun toggleLike() {
        viewModelScope.launch {
            try {
                val res = api.togglePostLike(currentPostId)
                isLiked = res.liked
                likeCount = res.likeCount
                if (res.liked) {
                    post?.let { p ->
                        if (p.authorId != currentUserId) {
                            insertLocalNotification(p.authorId, "LIKE", currentPostId, p.title,
                                "$currentNickname 님이 회원님의 게시글을 좋아합니다.")
                            NotificationHelper.send(getApplication(), "좋아요",
                                "$currentNickname 님이 게시글을 좋아합니다.",
                                System.currentTimeMillis().toInt())
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleScrap() {
        viewModelScope.launch {
            try {
                val res = api.togglePostScrap(currentPostId)
                isScrapped = res.scraped
            } catch (_: Exception) {}
        }
    }

    fun toggleFollow() {
        val authorId = post?.authorId ?: return
        viewModelScope.launch {
            try {
                val res = api.toggleFollow(authorId)
                isFollowing = res.followed
                if (res.followed) {
                    insertLocalNotification(authorId, "FOLLOW", -1, "",
                        "$currentNickname 님이 회원님을 팔로우하기 시작했습니다.")
                    NotificationHelper.send(getApplication(), "새 팔로워",
                        "$currentNickname 님이 팔로우했습니다.",
                        System.currentTimeMillis().toInt())
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleCommentLike(commentId: Int) {
        viewModelScope.launch {
            try {
                val res = api.toggleCommentLike(commentId)
                commentLikedByMe = commentLikedByMe + (commentId to res.liked)
                commentLikeCounts = commentLikeCounts + (commentId to res.likeCount)
            } catch (_: Exception) {}
        }
    }

    fun onCommentInputChange(value: String) { commentInput = value }

    fun setReplyTo(comment: CommentResponse?) { replyToComment = comment; commentInput = "" }

    fun submitComment() {
        val text = commentInput.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                val parent = replyToComment
                if (parent != null) {
                    api.createReply(currentPostId, parent.id, CommentRequest(text))
                } else {
                    api.createComment(currentPostId, CommentRequest(text))
                }
                commentInput = ""
                replyToComment = null
                loadComments()
                post?.let { p ->
                    if (p.authorId != currentUserId) {
                        insertLocalNotification(p.authorId, "COMMENT", currentPostId, p.title,
                            "$currentNickname 님이 댓글을 남겼습니다: $text")
                        NotificationHelper.send(getApplication(), "새 댓글",
                            "$currentNickname: $text", System.currentTimeMillis().toInt())
                    }
                    if (parent != null && parent.authorId != currentUserId) {
                        insertLocalNotification(parent.authorId, "COMMENT", currentPostId, p.title,
                            "$currentNickname 님이 회원님의 댓글에 답글을 남겼습니다: $text")
                        NotificationHelper.send(getApplication(), "새 답글",
                            "$currentNickname: $text", System.currentTimeMillis().toInt())
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun deletePost(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                api.deletePost(currentPostId)
                onDeleted()
            } catch (_: Exception) {}
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            try {
                api.deleteComment(commentId)
                loadComments()
            } catch (_: Exception) {}
        }
    }

    private suspend fun insertLocalNotification(
        targetUserId: Int, type: String, postId: Int, postTitle: String, message: String
    ) {
        val n = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        notificationDao.insert(AppNotification(
            targetUserId = targetUserId,
            type = type,
            fromUserId = currentUserId,
            fromNickname = currentNickname,
            postId = postId,
            postTitle = postTitle,
            message = message,
            createdAt = n,
        ))
    }
}
