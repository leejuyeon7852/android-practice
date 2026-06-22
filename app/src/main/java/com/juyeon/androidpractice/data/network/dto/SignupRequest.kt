package com.juyeon.androidpractice.data.network.dto

import com.google.gson.annotations.SerializedName
import com.juyeon.androidpractice.data.db.entity.User

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

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
)

data class UserResponse(
    val id: Int,
    val username: String,
    val nickname: String,
    val email: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

data class UserProfileResponse(
    val id: Int,
    val username: String,
    val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    @SerializedName("follower_count") val followerCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
)

fun UserProfileResponse.toUser() = User(
    id = id,
    userId = username,
    nickname = nickname,
    profileImageUri = profileImageUrl,
    followerCount = followerCount,
    followingCount = followingCount,
)