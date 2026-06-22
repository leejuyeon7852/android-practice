package com.juyeon.androidpractice.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.PostResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val api = ApiClient.api

    var posts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun loadFeed() {
        viewModelScope.launch {
            isLoading = true
            try {
                posts = api.getFeed()
            } catch (e: Exception) {
                // 피드 로드 실패 시 빈 목록 유지
            } finally {
                isLoading = false
            }
        }
    }
}
