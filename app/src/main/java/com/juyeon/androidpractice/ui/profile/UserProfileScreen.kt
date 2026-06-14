package com.juyeon.androidpractice.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.ui.search.PostCard
import com.juyeon.androidpractice.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    targetUserId: Int,
    currentUser: User,
    onBack: () -> Unit,
    onPostClick: (Int) -> Unit = {},
    viewModel: UserProfileViewModel = viewModel()
) {
    LaunchedEffect(targetUserId) {
        viewModel.init(targetUserId, currentUser.id)
    }

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val followerCount by viewModel.followerCount.collectAsStateWithLifecycle()
    val followingCount by viewModel.followingCount.collectAsStateWithLifecycle()
    val user = viewModel.targetUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.nickname ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 프로필 이미지
                    if (user?.profileImageUri != null) {
                        AsyncImage(
                            model = user.profileImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = user?.nickname ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // 팔로워/팔로잉 수
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$followerCount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "팔로워", fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$followingCount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "팔로잉", fontSize = 12.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${posts.size}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "게시글", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 팔로우 버튼
                    Button(
                        onClick = { viewModel.toggleFollow(currentUser.id, currentUser.nickname) },
                        shape = RoundedCornerShape(20.dp),
                        colors = if (viewModel.isFollowing)
                            ButtonDefaults.buttonColors(containerColor = GradientStart)
                        else
                            ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (viewModel.isFollowing) "팔로잉" else "팔로우",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "게시글",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("아직 작성한 게시글이 없습니다.", color = Color.Gray)
                    }
                }
            } else {
                items(posts, key = { it.id }) { post ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        PostCard(post = post, onClick = { onPostClick(post.id) })
                    }
                }
            }
        }
    }
}
