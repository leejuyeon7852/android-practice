package com.juyeon.androidpractice.repository.search

import com.juyeon.androidpractice.model.PostItem

interface SearchRepository {
    fun getPosts(): List<PostItem>
}