package com.juyeon.androidpractice.model

data class User(
    val id: Int = 0,          // RoomDB PK
    val userId: String,       // 로그인 아이디
    val password: String,     // 백엔드 연동 시 제거
    val nickname: String,
    val email: String,
    val nationality: String = "",
    val gender: String = "",
)
