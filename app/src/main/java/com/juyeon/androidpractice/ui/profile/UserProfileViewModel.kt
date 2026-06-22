package com.juyeon.androidpractice.ui.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.AppNotification
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.data.network.dto.UserProfileResponse
import com.juyeon.androidpractice.data.notification.NotificationHelper
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient.api
    private val notificationDao = AppDatabase.getInstance(application).notificationDao()

    var targetUser by mutableStateOf<UserProfileResponse?>(null)
        private set
    var posts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var isFollowing by mutableStateOf(false)
        private set

    fun init(targetUserId: Int, currentUserId: Int) {
        viewModelScope.launch {
            try {
                targetUser = api.getUser(targetUserId)
                posts = api.getUserPosts(targetUserId)
                if (targetUserId != currentUserId) {
                    isFollowing = api.getFollowStatus(targetUserId).followed
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleFollow(currentUserId: Int, currentNickname: String) {
        val tid = targetUser?.id ?: return
        if (tid == currentUserId) return
        viewModelScope.launch {
            try {
                val res = api.toggleFollow(tid)
                isFollowing = res.followed
                if (res.followed) {
                    val n = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    notificationDao.insert(AppNotification(
                        targetUserId = tid,
                        type = "FOLLOW",
                        fromUserId = currentUserId,
                        fromNickname = currentNickname,
                        message = "$currentNickname 님이 회원님을 팔로우하기 시작했습니다.",
                        createdAt = n,
                    ))
                    NotificationHelper.send(
                        context = getApplication(),
                        title = "새 팔로워",
                        body = "$currentNickname 님이 팔로우했습니다.",
                        id = System.currentTimeMillis().toInt()
                    )
                }
            } catch (_: Exception) {}
        }
    }
}
