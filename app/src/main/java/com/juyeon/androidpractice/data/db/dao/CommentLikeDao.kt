package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.CommentLike
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentLikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: CommentLike)

    @Query("DELETE FROM comment_likes WHERE userId = :userId AND commentId = :commentId")
    suspend fun delete(userId: Int, commentId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM comment_likes WHERE userId = :userId AND commentId = :commentId)")
    suspend fun isLiked(userId: Int, commentId: Int): Boolean

    @Query("SELECT COUNT(*) FROM comment_likes WHERE commentId = :commentId")
    fun getLikeCount(commentId: Int): Flow<Int>
}
