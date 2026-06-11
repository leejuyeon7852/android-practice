package com.juyeon.androidpractice.model

data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val message: String,
    val time: String,
    val isRead: Boolean
)

enum class NotificationType {
    COMMENT, LIKE, FOLLOW
}
