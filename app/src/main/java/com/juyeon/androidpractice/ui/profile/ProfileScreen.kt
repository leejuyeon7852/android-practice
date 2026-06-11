package com.juyeon.androidpractice.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juyeon.androidpractice.ui.theme.GradientStart

@Composable
fun ProfileScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("쓴 글", "댓글", "스크랩", "좋아요")

    Column(modifier = Modifier.fillMaxSize()) {

        // 설정 아이콘
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "설정",
                    tint = Color.Gray
                )
            }
        }

        // 프로필 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "닉네임", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                FollowCount(label = "팔로워", count = 0)
                FollowCount(label = "팔로잉", count = 0)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = GradientStart
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) GradientStart else Color.Gray,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val message = when (selectedTab) {
                0 -> "작성한 글이 없습니다."
                1 -> "작성한 댓글이 없습니다."
                2 -> "스크랩한 글이 없습니다."
                3 -> "좋아요한 글이 없습니다."
                else -> ""
            }
            Text(text = message, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun FollowCount(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
