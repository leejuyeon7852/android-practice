package com.juyeon.androidpractice.ui.alarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.alarm.AlarmReceiver
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.model.NotificationItem
import com.juyeon.androidpractice.model.NotificationType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = AppDatabase.getInstance(application).notificationDao()

    private val _currentUserId = MutableStateFlow(-1)

    fun setCurrentUser(userId: Int) { _currentUserId.value = userId }

    val notifications: StateFlow<List<NotificationItem>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == -1) flowOf(emptyList())
            else notificationDao.getAll(uid).map { list ->
                list.map { n ->
                    NotificationItem(
                        id = n.id,
                        type = when (n.type) {
                            "LIKE" -> NotificationType.LIKE
                            "FOLLOW" -> NotificationType.FOLLOW
                            else -> NotificationType.COMMENT
                        },
                        message = n.message,
                        time = n.createdAt,
                        isRead = n.isRead,
                        postId = n.postId
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == -1) flowOf(0)
            else notificationDao.getUnreadCount(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(id: Int) {
        viewModelScope.launch { notificationDao.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { notificationDao.markAllAsRead() }
    }

    fun setAlarm(hour: Int, minute: Int) {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    fun cancelAlarm() {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
