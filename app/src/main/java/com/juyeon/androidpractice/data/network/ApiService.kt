package com.juyeon.androidpractice.data.network

import com.juyeon.androidpractice.data.network.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("users/signup")
    suspend fun signup(@Body body: SignupRequest): UserResponse

    @POST("users/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @GET("users/me")
    suspend fun getMe(): UserProfileResponse

    // 내 정보
    @Multipart
    @PUT("users/me")
    suspend fun updateMe(
        @Part("nickname") nickname: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): UserProfileResponse

    @GET("users/me/posts")
    suspend fun getMyPosts(): List<PostResponse>

    @GET("users/me/comments")
    suspend fun getMyComments(): List<CommentResponse>

    @GET("users/me/likes")
    suspend fun getMyLikes(): List<PostResponse>

    @GET("users/me/scraps")
    suspend fun getMyScraps(): List<PostResponse>

    // 유저
    @GET("users/search")
    suspend fun searchUsers(@Query("q") q: String): List<UserResponse>

    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Int): UserProfileResponse

    @GET("users/{user_id}/posts")
    suspend fun getUserPosts(@Path("user_id") userId: Int): List<PostResponse>

    // 팔로우
    @POST("users/{user_id}/follow")
    suspend fun toggleFollow(@Path("user_id") userId: Int): FollowToggleResponse

    @GET("users/{user_id}/follow/status")
    suspend fun getFollowStatus(@Path("user_id") userId: Int): FollowStatusResponse

    @GET("users/me/followers")
    suspend fun getMyFollowers(): List<FollowUserResponse>

    @GET("users/me/following")
    suspend fun getMyFollowing(): List<FollowUserResponse>

    // 게시글
    @GET("posts/feed")
    suspend fun getFeed(): List<PostResponse>

    @GET("posts/")
    suspend fun getPosts(): List<PostResponse>

    @GET("posts/search")
    suspend fun searchPosts(@Query("q") q: String): List<PostResponse>

    @GET("posts/{post_id}")
    suspend fun getPost(@Path("post_id") postId: Int): PostResponse

    @Multipart
    @POST("posts/")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?,
    ): PostResponse

    @Multipart
    @PUT("posts/{post_id}")
    suspend fun updatePost(
        @Path("post_id") postId: Int,
        @Part("title") title: RequestBody?,
        @Part("body") body: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): PostResponse

    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Int)

    // 좋아요 / 스크랩
    @GET("posts/{post_id}/like/status")
    suspend fun getLikeStatus(@Path("post_id") postId: Int): LikeStatusResponse

    @POST("posts/{post_id}/like")
    suspend fun togglePostLike(@Path("post_id") postId: Int): LikeToggleResponse

    @GET("posts/{post_id}/scrap/status")
    suspend fun getScrapStatus(@Path("post_id") postId: Int): ScrapStatusResponse

    @POST("posts/{post_id}/scrap")
    suspend fun togglePostScrap(@Path("post_id") postId: Int): ScrapStatusResponse

    @POST("comments/{comment_id}/like")
    suspend fun toggleCommentLike(@Path("comment_id") commentId: Int): LikeToggleResponse

    // 댓글
    @GET("posts/{post_id}/comments")
    suspend fun getComments(@Path("post_id") postId: Int): List<CommentResponse>

    @POST("posts/{post_id}/comments")
    suspend fun createComment(
        @Path("post_id") postId: Int,
        @Body body: CommentRequest,
    ): CommentResponse

    @POST("posts/{post_id}/comments/{comment_id}/replies")
    suspend fun createReply(
        @Path("post_id") postId: Int,
        @Path("comment_id") commentId: Int,
        @Body body: CommentRequest,
    ): CommentResponse

    @DELETE("comments/{comment_id}")
    suspend fun deleteComment(@Path("comment_id") commentId: Int)
}