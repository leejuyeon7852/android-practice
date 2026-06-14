package com.juyeon.androidpractice.ui.alarm

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juyeon.androidpractice.model.NotificationItem
import com.juyeon.androidpractice.model.NotificationType
import com.juyeon.androidpractice.ui.theme.GradientStart

@Composable
fun AlarmScreen(viewModel: AlarmViewModel = viewModel()) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // 알림 탭 진입 시 전체 읽음 처리 (선택사항 — 클릭해서 개별 읽음도 가능)
    // LaunchedEffect(Unit) { viewModel.markAllAsRead() }

    Column(modifier = Modifier.fillMaxSize()) {

        // 알림 리스트
        Text(
            text = "알림",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notifications) { item ->
                NotificationCard(item, onClick = { if (!item.isRead) viewModel.markAsRead(item.id) })
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 알람 설정
        Text(
            text = "알람 설정",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 시간 선택
            NumberPicker(
                value = selectedHour,
                range = 0..23,
                label = "시",
                onValueChange = { selectedHour = it },
                modifier = Modifier.weight(1f)
            )
            NumberPicker(
                value = selectedMinute,
                range = 0..59,
                label = "분",
                onValueChange = { selectedMinute = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.setAlarm(selectedHour, selectedMinute) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                modifier = Modifier.weight(1f)
            ) {
                Text("알람 설정", color = Color.White)
            }
            OutlinedButton(
                onClick = { viewModel.cancelAlarm() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("알람 취소")
            }
        }

        // 알람 권한 설정 버튼 (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text("정확한 알람 권한 설정")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NotificationCard(item: NotificationItem, onClick: () -> Unit = {}) {
    val backgroundColor = if (item.isRead) Color.White else Color(0xFFEDF5FB)
    val icon: ImageVector = when (item.type) {
        NotificationType.COMMENT -> Icons.Filled.Notifications
        NotificationType.LIKE -> Icons.Filled.Favorite
        NotificationType.FOLLOW -> Icons.Filled.Person
    }
    val iconColor: Color = when (item.type) {
        NotificationType.COMMENT -> GradientStart
        NotificationType.LIKE -> Color(0xFFE53935)
        NotificationType.FOLLOW -> Color(0xFF43A047)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.message, fontSize = 14.sp, fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Normal)
                Text(text = item.time, fontSize = 12.sp, color = Color.Gray)
            }
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(GradientStart, RoundedCornerShape(50))
                )
            }
        }
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
                Text("-", fontSize = 20.sp)
            }
            Text(text = "%02d $label".format(value), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            TextButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
                Text("+", fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlarmScreenPreview() {
    AlarmScreen()
}
