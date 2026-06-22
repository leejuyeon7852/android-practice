package com.juyeon.androidpractice.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.data.network.dto.UserResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val api = ApiClient.api

    var searchQuery by mutableStateOf("")
        private set
    var selectedTab by mutableIntStateOf(0)
        private set
    var posts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var users by mutableStateOf<List<UserResponse>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    fun onSearchChange(q: String) {
        searchQuery = q
        val trimmed = q.trim()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (trimmed.isBlank()) {
                posts = emptyList()
                users = emptyList()
            } else {
                try {
                    errorMessage = null
                    if (selectedTab == 0) posts = api.searchPosts(trimmed)
                    else users = api.searchUsers(trimmed)
                } catch (e: Exception) {
                    errorMessage = e.message ?: e.javaClass.simpleName
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        selectedTab = index
        if (searchQuery.isNotBlank()) onSearchChange(searchQuery)
    }
}
