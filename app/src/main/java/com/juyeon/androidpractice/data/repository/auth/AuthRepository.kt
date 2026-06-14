package com.juyeon.androidpractice.data.repository.auth

import com.juyeon.androidpractice.data.db.entity.User

interface AuthRepository {
    suspend fun login(userId: String, password: String): User?
    suspend fun signup(user: User): Boolean  // false = 아이디 중복
    suspend fun updateUser(user: User)
}
