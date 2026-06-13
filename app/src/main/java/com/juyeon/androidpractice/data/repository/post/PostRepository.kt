package com.juyeon.androidpractice.data.repository.post

import com.juyeon.androidpractice.data.db.entity.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPosts(): Flow<List<Post>>
    fun getPostsByAuthor(authorId: Int): Flow<List<Post>>
    suspend fun getPostById(postId: Int): Post?
    suspend fun savePost(post: Post): Long
    suspend fun deletePost(postId: Int)
}
