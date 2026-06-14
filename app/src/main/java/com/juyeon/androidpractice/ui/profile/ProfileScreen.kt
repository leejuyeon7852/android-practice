package com.juyeon.androidpractice.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
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
import com.juyeon.androidpractice.data.db.entity.Comment
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.ui.search.PostCard
import com.juyeon.androidpractice.ui.theme.GradientStart

@Composable
fun ProfileScreen(
    currentUser: User,
    onPostClick: (Int) -> Unit = {},
    onUserUpdated: (User) -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) { viewModel.init(currentUser.id) }

    val myPosts by viewModel.myPosts.collectAsStateWithLifecycle()
    val myComments by viewModel.myComments.collectAsStateWithLifecycle()
    val scrappedPosts by viewModel.scrappedPosts.collectAsStateWithLifecycle()
    val likedPosts by viewModel.likedPosts.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var editNickname by remember(currentUser.nickname) { mutableStateOf(currentUser.nickname) }
    var editImageUri by remember(currentUser.profileImageUri) { mutableStateOf(currentUser.profileImageUri) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { editImageUri = it.toString() } }

    val tabs = listOf("쓴 글", "댓글", "스크랩", "좋아요")

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("정말 로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text("로그아웃", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("취소") }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("프로필 수정") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (editImageUri != null) {
                            AsyncImage(
                                model = editImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("닉네임") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated = currentUser.copy(
                            nickname = editNickname.trim().ifBlank { currentUser.nickname },
                            profileImageUri = editImageUri
                        )
                        viewModel.updateProfile(updated) { onUserUpdated(it) }
                        showEditDialog = false
                    }
                ) { Text("저장") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("취소") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box {
                IconButton(onClick = { showSettingsMenu = true }) {
                    Icon(Icons.Filled.Settings, contentDescription = "설정", tint = Color.Gray)
                }
                DropdownMenu(
                    expanded = showSettingsMenu,
                    onDismissRequest = { showSettingsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("프로필 수정") },
                        onClick = { showSettingsMenu = false; showEditDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text("로그아웃", color = MaterialTheme.colorScheme.error) },
                        onClick = { showSettingsMenu = false; showLogoutDialog = true }
                    )
                }
            }
        }

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
                    .clickable { showEditDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (currentUser.profileImageUri != null) {
                    AsyncImage(
                        model = currentUser.profileImageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = currentUser.nickname, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

        when (selectedTab) {
            0 -> PostListContent(posts = myPosts, emptyMessage = "작성한 글이 없습니다.", onPostClick = onPostClick)
            1 -> CommentListContent(comments = myComments)
            2 -> PostListContent(posts = scrappedPosts, emptyMessage = "스크랩한 글이 없습니다.", onPostClick = onPostClick)
            3 -> PostListContent(posts = likedPosts, emptyMessage = "좋아요한 글이 없습니다.", onPostClick = onPostClick)
        }
    }
}

@Composable
private fun PostListContent(posts: List<Post>, emptyMessage: String, onPostClick: (Int) -> Unit) {
    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = emptyMessage, color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(posts) { post ->
                PostCard(post = post, onClick = { onPostClick(post.id) })
            }
        }
    }
}

@Composable
private fun CommentListContent(comments: List<Comment>) {
    if (comments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "작성한 댓글이 없습니다.", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(comments) { comment ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = comment.body, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comment.createdAt, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
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
