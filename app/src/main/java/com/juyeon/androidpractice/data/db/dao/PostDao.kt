package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.juyeon.androidpractice.data.db.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: Post): Long

    @Update
    suspend fun update(post: Post)

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY id DESC")
    fun getPostsByAuthor(authorId: Int): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Int): Post?

    @Query("SELECT * FROM posts WHERE id IN (:postIds) ORDER BY id DESC")
    fun getPostsByIds(postIds: List<Int>): Flow<List<Post>>

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)
}
