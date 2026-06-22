package com.juyeon.androidpractice.data.network.dto

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    val id: Int,
    @SerializedName("post_id") val postId: Int,
    @SerializedName("author_id") val authorId: Int,
    @SerializedName("author_nickname") val authorNickname: String,
    @SerializedName("parent_id") val parentId: Int?,
    val body: String,
    @SerializedName("created_at") val createdAt: String,
    val replies: List<CommentResponse> = emptyList(),
)

data class CommentRequest(val body: String)
