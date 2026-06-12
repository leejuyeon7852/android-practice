package com.juyeon.androidpractice.model

data class WriteItem (
    val id: Int,
    val title: String,
    val body: String,
    val author: String,
    val createdAt: String,
    val imageUri: String? = null,
)