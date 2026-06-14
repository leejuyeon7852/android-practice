package com.juyeon.androidpractice.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetUserId: Int,      // 알림 받을 사람 (게시글 작성자)
    val type: String,           // "LIKE", "COMMENT", "FOLLOW"
    val fromUserId: Int,
    val fromNickname: String,
    val postId: Int = -1,
    val postTitle: String = "",
    val message: String,
    val createdAt: String,
    val isRead: Boolean = false
)
