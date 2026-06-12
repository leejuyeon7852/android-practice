package com.juyeon.androidpractice.model

data class PostItem(
    val id: Int,
    val title: String,
    val author: String,
    val imageUrl: String? = null,
)
