package com.juyeon.androidpractice.data.repository.auth

import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.LoginRequest
import com.juyeon.androidpractice.data.network.SignupRequest
import com.juyeon.androidpractice.data.network.TokenManager
import com.juyeon.androidpractice.data.network.toEntity

class AuthRepositoryRemoteImpl(
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun login(userId: String, password: String): User? {
        return try {
            val token = ApiClient.api.login(LoginRequest(userId, password))
            tokenManager.save(token.accessToken)
            val me = ApiClient.api.getMe()
            me.toEntity()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signup(user: User): Boolean {
        return try {
            ApiClient.api.signup(
                SignupRequest(
                    username = user.userId,
                    nickname = user.nickname,
                    email = user.email,
                    password = user.password,
                )
            )
            true
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 400) false else throw e
        }
    }

    override suspend fun updateUser(user: User) {
        // TODO: 백엔드 프로필 수정 API 추가 후 구현
    }
}
