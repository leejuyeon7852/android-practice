package com.juyeon.androidpractice.data.network.dto

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: Int,
    @SerializedName("author_id") val authorId: Int,
    @SerializedName("author_nickname") val authorNickname: String,
    val title: String,
    val body: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String,
)

data class LikeStatusResponse(
    val liked: Boolean,
    @SerializedName("like_count") val likeCount: Int,
)

data class LikeToggleResponse(
    val liked: Boolean,
    @SerializedName("like_count") val likeCount: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_nickname") val userNickname: String,
    @SerializedName("post_id") val postId: Int?,
    @SerializedName("comment_id") val commentId: Int?,
)

data class ScrapStatusResponse(
    val scraped: Boolean,
    @SerializedName("scrap_count") val scrapCount: Int,
)
