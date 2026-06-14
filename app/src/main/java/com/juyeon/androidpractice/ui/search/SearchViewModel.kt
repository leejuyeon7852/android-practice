package com.juyeon.androidpractice.ui.search

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.repository.post.PostRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PostRepositoryImpl(db.postDao())
    private val userDao = db.userDao()

    private val _searchQuery = MutableStateFlow("")
    var searchQuery by mutableStateOf("")
        private set

    var selectedTab by mutableIntStateOf(0)
        private set

    fun onSearchChange(q: String) {
        searchQuery = q
        _searchQuery.value = q
    }

    fun onTabSelected(index: Int) {
        selectedTab = index
    }

    val posts: StateFlow<List<Post>> = combine(
        repository.getAllPosts(),
        _searchQuery
    ) { posts, q ->
        if (q.isBlank()) posts
        else posts.filter {
            it.title.contains(q, ignoreCase = true) || it.body.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val users: StateFlow<List<User>> = _searchQuery
        .flatMapLatest { q ->
            if (q.isBlank()) kotlinx.coroutines.flow.flowOf(emptyList())
            else userDao.searchByNickname(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
