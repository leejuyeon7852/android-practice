package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.Follow
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun follow(follow: Follow)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followeeId = :followeeId")
    suspend fun unfollow(followerId: Int, followeeId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followeeId = :followeeId)")
    suspend fun isFollowing(followerId: Int, followeeId: Int): Boolean

    @Query("SELECT COUNT(*) FROM follows WHERE followeeId = :userId")
    fun getFollowerCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId")
    fun getFollowingCount(userId: Int): Flow<Int>

    // 내가 팔로우하는 사람 ID 목록 (실시간)
    @Query("SELECT followeeId FROM follows WHERE followerId = :userId")
    fun getFollowingIds(userId: Int): Flow<List<Int>>

    // 나를 팔로우하는 사람 ID 목록 (실시간)
    @Query("SELECT followerId FROM follows WHERE followeeId = :userId")
    fun getFollowerIds(userId: Int): Flow<List<Int>>
}
