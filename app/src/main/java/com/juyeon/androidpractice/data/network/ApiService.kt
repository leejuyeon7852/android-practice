package com.juyeon.androidpractice.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// --- Request DTOs ---

data class SignupRequest(
    val username: String,
    val nickname: String,
    val email: String,
    val password: String,
)

data class LoginRequest(
    val username: String,
    val password: String,
)

// --- Response DTOs ---

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
)

data class UserResponse(
    val id: Int,
    val username: String,
    val nickname: String,
    val email: String,
    @SerializedName("created_at") val createdAt: String,
)

// --- Mapper ---

fun UserResponse.toEntity() = com.juyeon.androidpractice.data.db.entity.User(
    id = id,
    userId = username,
    password = "",   // 서버에서 받아오지 않음
    nickname = nickname,
    email = email,
)

// --- Endpoints ---

interface ApiService {
    @POST("users/signup")
    suspend fun signup(@Body body: SignupRequest): UserResponse

    @POST("users/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @GET("users/me")
    suspend fun getMe(): UserResponse
}
