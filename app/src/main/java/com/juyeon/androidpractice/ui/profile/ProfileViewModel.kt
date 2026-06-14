package com.juyeon.androidpractice.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.Comment
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _userId = MutableStateFlow(-1)

    val myPosts: StateFlow<List<Post>> = _userId
        .filter { it != -1 }
        .flatMapLatest { db.postDao().getPostsByAuthor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myComments: StateFlow<List<Comment>> = _userId
        .filter { it != -1 }
        .flatMapLatest { db.commentDao().getCommentsByAuthor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scrappedPosts: StateFlow<List<Post>> = _userId
        .filter { it != -1 }
        .flatMapLatest { uid ->
            db.scrapDao().getScrappedPostIds(uid).flatMapLatest { ids ->
                if (ids.isEmpty()) flowOf(emptyList())
                else db.postDao().getPostsByIds(ids)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedPosts: StateFlow<List<Post>> = _userId
        .filter { it != -1 }
        .flatMapLatest { uid ->
            db.likeDao().getLikedPostIds(uid).flatMapLatest { ids ->
                if (ids.isEmpty()) flowOf(emptyList())
                else db.postDao().getPostsByIds(ids)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followerCount: StateFlow<Int> = _userId
        .filter { it != -1 }
        .flatMapLatest { db.followDao().getFollowerCount(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val followingCount: StateFlow<Int> = _userId
        .filter { it != -1 }
        .flatMapLatest { db.followDao().getFollowingCount(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val followerUsers: StateFlow<List<com.juyeon.androidpractice.data.db.entity.User>> = _userId
        .filter { it != -1 }
        .flatMapLatest { uid ->
            db.followDao().getFollowerIds(uid).map { ids ->
                if (ids.isEmpty()) emptyList() else db.userDao().getUsersByIds(ids)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingUsers: StateFlow<List<com.juyeon.androidpractice.data.db.entity.User>> = _userId
        .filter { it != -1 }
        .flatMapLatest { uid ->
            db.followDao().getFollowingIds(uid).map { ids ->
                if (ids.isEmpty()) emptyList() else db.userDao().getUsersByIds(ids)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun init(userId: Int) {
        if (_userId.value != userId) _userId.value = userId
    }

    fun updateProfile(user: User, onDone: (User) -> Unit) {
        viewModelScope.launch {
            db.userDao().update(user)
            onDone(user)
        }
    }
}
