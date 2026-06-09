package com.juyeon.androidpractice.repository

import com.juyeon.androidpractice.model.PostItem

interface SearchRepository {
    fun getPosts(): List<PostItem>
}
