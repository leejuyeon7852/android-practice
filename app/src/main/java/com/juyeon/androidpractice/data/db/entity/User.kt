package com.juyeon.androidpractice.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val password: String,     // 백엔드 연동 시 제거
    val nickname: String,
    val email: String,
    val nationality: String = "",
    val gender: String = "",
    val profileImageUri: String? = null,
)
