package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.Scrap
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrapDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scrap: Scrap)

    @Query("DELETE FROM scraps WHERE userId = :userId AND postId = :postId")
    suspend fun delete(userId: Int, postId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM scraps WHERE userId = :userId AND postId = :postId)")
    suspend fun isScrapped(userId: Int, postId: Int): Boolean

    @Query("SELECT postId FROM scraps WHERE userId = :userId")
    fun getScrappedPostIds(userId: Int): Flow<List<Int>>
}
