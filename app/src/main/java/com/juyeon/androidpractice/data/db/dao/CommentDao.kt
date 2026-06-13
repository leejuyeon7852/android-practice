package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insert(comment: Comment)

    @Query("SELECT * FROM comments WHERE postId = :postId AND parentCommentId IS NULL ORDER BY id ASC")
    fun getTopLevelComments(postId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE parentCommentId = :commentId ORDER BY id ASC")
    fun getReplies(commentId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE authorId = :authorId ORDER BY id DESC")
    fun getCommentsByAuthor(authorId: Int): Flow<List<Comment>>

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Int)
}
