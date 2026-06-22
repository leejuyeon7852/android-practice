package com.juyeon.androidpractice.data.network.dto

import com.google.gson.annotations.SerializedName

data class FollowToggleResponse(
    val followed: Boolean,
    @SerializedName("following_id") val followingId: Int,
)

data class FollowStatusResponse(
    val followed: Boolean,
    @SerializedName("following_id") val followingId: Int,
)

data class FollowUserResponse(
    val id: Int,
    val username: String,
    val nickname: String,
)
