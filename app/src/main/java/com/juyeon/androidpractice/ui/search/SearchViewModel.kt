package com.juyeon.androidpractice.ui.search

import androidx.lifecycle.ViewModel
import com.juyeon.androidpractice.model.PostItem
import com.juyeon.androidpractice.repository.MockSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel : ViewModel() {
    private val repository = MockSearchRepository()

    private val _posts = MutableStateFlow<List<PostItem>>(emptyList())
    val posts: StateFlow<List<PostItem>> = _posts

    init {
        _posts.value = repository.getPosts()
    }
}
