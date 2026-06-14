package com.juyeon.androidpractice.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.ui.theme.GradientStart

@Composable
fun SearchScreen(
    onPostClick: (postId: Int) -> Unit = {},
    onUserClick: (userId: Int) -> Unit = {},
    viewModel: SearchViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = { Text(if (viewModel.selectedTab == 0) "게시글 검색..." else "닉네임 검색...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TabRow(
            selectedTabIndex = viewModel.selectedTab,
            containerColor = Color.Transparent,
            contentColor = GradientStart
        ) {
            Tab(
                selected = viewModel.selectedTab == 0,
                onClick = { viewModel.onTabSelected(0) },
                text = { Text("게시글") }
            )
            Tab(
                selected = viewModel.selectedTab == 1,
                onClick = { viewModel.onTabSelected(1) },
                text = { Text("유저") }
            )
        }

        if (viewModel.selectedTab == 0) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    PostCard(post = post, onClick = { onPostClick(post.id) })
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(users) { user ->
                    UserCard(user = user, onClick = { onUserClick(user.id) })
                }
                if (users.isEmpty() && viewModel.searchQuery.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("검색 결과가 없습니다.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user.profileImageUri != null) {
            AsyncImage(
                model = user.profileImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {}
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = user.nickname, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

@Composable
fun PostCard(post: Post, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = post.title.trim(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.authorNickname.trim(),
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (post.imageUri != null) {
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = post.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
