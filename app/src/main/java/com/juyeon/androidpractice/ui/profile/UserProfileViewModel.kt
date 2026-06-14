package com.juyeon.androidpractice.ui.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.Follow
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.db.entity.AppNotification
import com.juyeon.androidpractice.data.notification.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    var targetUser by mutableStateOf<User?>(null)
        private set
    var isFollowing by mutableStateOf(false)
        private set

    private val _targetUserId = MutableStateFlow(-1)

    val posts: StateFlow<List<Post>> = _targetUserId
        .filter { it != -1 }
        .flatMapLatest { db.postDao().getPostsByAuthor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followerCount: StateFlow<Int> = _targetUserId
        .filter { it != -1 }
        .flatMapLatest { db.followDao().getFollowerCount(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val followingCount: StateFlow<Int> = _targetUserId
        .filter { it != -1 }
        .flatMapLatest { db.followDao().getFollowingCount(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun init(targetUserId: Int, currentUserId: Int) {
        if (_targetUserId.value == targetUserId) return
        _targetUserId.value = targetUserId
        viewModelScope.launch {
            targetUser = db.userDao().findById(targetUserId)
            isFollowing = db.followDao().isFollowing(currentUserId, targetUserId)
        }
    }

    fun toggleFollow(currentUserId: Int, currentNickname: String) {
        val tid = _targetUserId.value
        if (tid == -1 || tid == currentUserId) return
        viewModelScope.launch {
            if (isFollowing) {
                db.followDao().unfollow(currentUserId, tid)
            } else {
                db.followDao().follow(Follow(currentUserId, tid))
                val n = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                db.notificationDao().insert(
                    AppNotification(
                        targetUserId = tid,
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
            isFollowing = !isFollowing
        }
    }
}
