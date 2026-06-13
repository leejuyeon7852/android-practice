package com.juyeon.androidpractice.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.data.db.entity.Post

@Composable
fun SearchScreen(
    onPostClick: (postId: Int) -> Unit = {},
    viewModel: SearchViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(posts) { post ->
            PostCard(post = post, onClick = { onPostClick(post.id) })
        }
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchScreenPreview() {
    val samplePosts = listOf(
        Post(
            id = 1,
            authorId = 1,
            authorNickname = "주연",
            title = "오늘 날씨가 정말 좋네요",
            body = "산책하기 딱 좋은 날입니다.",
            imageUri = null,
            createdAt = "2026-06-13 10:00"
        ),
        Post(
            id = 2,
            authorId = 2,
            authorNickname = "민수",
            title = "제목이 조금 긴 게시글은 어떻게 보일까요 한번 확인해봅시다",
            body = "내용",
            imageUri = null,
            createdAt = "2026-06-13 11:30"
        ),
        Post(
            id = 3,
            authorId = 3,
            authorNickname = "하늘",
            title = "짧은 제목",
            body = "내용",
            imageUri = null,
            createdAt = "2026-06-13 12:00"
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(samplePosts) { post ->
            PostCard(post = post)
        }
    }
}
