package com.juyeon.androidpractice.data.db.entity

data class User(
    val id: Int = 0,
    val userId: String = "",
    val password: String = "",
    val nickname: String = "",
    val email: String = "",
    val profileImageUri: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
)
