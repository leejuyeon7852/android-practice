package com.juyeon.androidpractice.repository.alarm

import com.juyeon.androidpractice.model.NotificationItem

interface AlarmRepository {
    fun getNotifications(): List<NotificationItem>
}