package com.juyeon.androidpractice.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val postDao = db.postDao()
    private val followDao = db.followDao()
    private val userDao = db.userDao()

    private val _currentUserId = MutableStateFlow(-1)

    var authorProfiles by mutableStateOf<Map<Int, String?>>(emptyMap())
        private set

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }

    val followingPosts: StateFlow<List<Post>> = _currentUserId
        .filter { it != -1 }
        .flatMapLatest { userId ->
            followDao.getFollowingIds(userId)
                .flatMapLatest { followingIds ->
                    if (followingIds.isEmpty()) flowOf(emptyList())
                    else postDao.getPostsByAuthorIds(followingIds)
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            followingPosts.collect { posts ->
                val ids = posts.map { it.authorId }.distinct()
                if (ids.isNotEmpty()) {
                    val users = userDao.getUsersByIds(ids)
                    authorProfiles = users.associate { it.id to it.profileImageUri }
                }
            }
        }
    }
}
