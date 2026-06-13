package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.Like
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: Like)

    @Query("DELETE FROM likes WHERE userId = :userId AND postId = :postId")
    suspend fun delete(userId: Int, postId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND postId = :postId)")
    suspend fun isLiked(userId: Int, postId: Int): Boolean

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun getLikeCount(postId: Int): Flow<Int>

    @Query("SELECT postId FROM likes WHERE userId = :userId")
    fun getLikedPostIds(userId: Int): Flow<List<Int>>
}
