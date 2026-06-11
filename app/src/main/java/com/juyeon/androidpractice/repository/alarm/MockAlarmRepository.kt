package com.juyeon.androidpractice.repository.alarm

import com.juyeon.androidpractice.model.NotificationItem
import com.juyeon.androidpractice.model.NotificationType

class MockAlarmRepository : AlarmRepository {
    override fun getNotifications() = listOf(
        NotificationItem(1, NotificationType.COMMENT, "홍길동님이 댓글을 달았습니다.", "5분 전", false),
        NotificationItem(2, NotificationType.LIKE, "내 글에 좋아요가 눌렸습니다.", "1시간 전", false),
        NotificationItem(3, NotificationType.FOLLOW, "새 팔로워가 생겼습니다.", "3시간 전", true),
        NotificationItem(4, NotificationType.COMMENT, "김철수님이 댓글을 달았습니다.", "어제", true),
        NotificationItem(5, NotificationType.LIKE, "내 댓글에 좋아요가 눌렸습니다.", "어제", true),
    )
}