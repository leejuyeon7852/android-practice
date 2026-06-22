package com.juyeon.androidpractice.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.ui.theme.GradientStart
import androidx.compose.foundation.Canvas

@Composable
fun HomeScreen(
    currentUserId: Int = -1,
    onPostClick: (postId: Int) -> Unit = {},
    onUserClick: (userId: Int) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(currentUserId) {
        if (currentUserId != -1) viewModel.loadFeed()
    }

    val posts = viewModel.posts
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (posts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("팔로잉한 유저의 게시글이 없습니다.", color = Color.Gray, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("다른 유저를 팔로우해보세요!", color = Color.LightGray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "팔로잉 피드",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(posts, key = { it.id }) { post ->
                    FeedPostCard(
                        post = post,
                        onClick = { onPostClick(post.id) },
                        onUserClick = { onUserClick(post.authorId) }
                    )
                }
            }
        }

        // 펼쳐지는 FAB (우측 하단)
        ExpandableFab(
            expanded = expanded,
            onToggle = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@Composable
private fun FeedPostCard(post: PostResponse, onClick: () -> Unit, onUserClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onUserClick)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = post.authorNickname, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    Text(text = post.createdAt, fontSize = 11.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (post.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.body,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

@Composable
private fun ExpandableFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fabItems = listOf("🔍", "✏️", "🔔")

    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        fabItems.forEachIndexed { index, icon ->
            val offsetY by animateDpAsState(
                targetValue = if (expanded) (-(index + 1) * 64).dp else 0.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "offsetY_$index"
            )
            val alpha by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                label = "alpha_$index"
            )

            Box(
                modifier = Modifier
                    .offset(y = offsetY)
                    .alpha(alpha)
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = GradientStart,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 45f else 0f,
                label = "rotation"
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}
