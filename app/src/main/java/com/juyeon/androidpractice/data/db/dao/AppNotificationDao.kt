package com.juyeon.androidpractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.juyeon.androidpractice.data.db.entity.AppNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNotificationDao {
    @Insert
    suspend fun insert(notification: AppNotification)

    @Query("SELECT * FROM app_notifications WHERE targetUserId = :userId ORDER BY id DESC")
    fun getAll(userId: Int): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE targetUserId = :userId AND isRead = 0")
    fun getUnreadCount(userId: Int): Flow<Int>

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllAsRead()
}
