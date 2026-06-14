package com.juyeon.androidpractice.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.layout.PaddingValues
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
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    currentUser: User,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = viewModel()
) {
    LaunchedEffect(postId) { viewModel.init(postId, currentUser.id) }

    val post = viewModel.post
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val replies by viewModel.replies.collectAsStateWithLifecycle()
    var showCommentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // 댓글 목록 바뀔 때 좋아요 상태 갱신
    val allCommentIds = (comments + replies.values.flatten()).map { it.id }
    LaunchedEffect(allCommentIds) {
        if (allCommentIds.isNotEmpty()) viewModel.refreshCommentInteractions(allCommentIds, currentUser.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시글") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        if (post == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(text = post.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.LightGray))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = post.authorNickname, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = post.createdAt, fontSize = 12.sp, color = Color.Gray)
                        if (post.authorId != currentUser.id) {
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                onClick = { viewModel.toggleFollow(currentUser.id, currentUser.nickname) },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = if (viewModel.isFollowing)
                                    ButtonDefaults.outlinedButtonColors(containerColor = GradientStart.copy(alpha = 0.1f))
                                else
                                    ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (viewModel.isFollowing) "팔로잉" else "팔로우",
                                    fontSize = 12.sp,
                                    color = if (viewModel.isFollowing) GradientStart else Color.Gray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (post.imageUri != null) {
                        AsyncImage(
                            model = post.imageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(text = post.body, fontSize = 16.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    // 액션 바
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 좋아요
                        IconButton(onClick = { viewModel.toggleLike(currentUser.id, currentUser.nickname) }) {
                            Icon(
                                if (viewModel.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (viewModel.isLiked) Color.Red else Color.Gray
                            )
                        }
                        Text("${viewModel.likeCount}", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        // 스크랩
                        IconButton(onClick = { viewModel.toggleScrap(currentUser.id) }) {
                            Icon(
                                if (viewModel.isScrapped) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "스크랩",
                                tint = if (viewModel.isScrapped) GradientStart else Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // 댓글
                        IconButton(onClick = { showCommentSheet = true }) {
                            Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "댓글", tint = Color.Gray)
                        }
                        Text("${comments.size}", fontSize = 14.sp, color = Color.Gray)
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    if (showCommentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCommentSheet = false; viewModel.setReplyTo(null) },
            sheetState = sheetState,
        ) {
            CommentSheetContent(
                comments = comments,
                replies = replies,
                commentLikeCounts = viewModel.commentLikeCounts,
                commentLikedByMe = viewModel.commentLikedByMe,
                replyToComment = viewModel.replyToComment,
                commentInput = viewModel.commentInput,
                currentUserId = currentUser.id,
                followingUserIds = viewModel.followingUserIds,
                onInputChange = viewModel::onCommentInputChange,
                onSubmit = { viewModel.submitComment(currentUser.id, currentUser.nickname) },
                onLikeComment = { viewModel.toggleCommentLike(it, currentUser.id) },
                onReplyTo = { viewModel.setReplyTo(it) },
                onCancelReply = { viewModel.setReplyTo(null) },
                onFollowUser = { targetId -> viewModel.toggleFollowUser(targetId, currentUser.id, currentUser.nickname) }
            )
        }
    }
}

@Composable
private fun CommentSheetContent(
    comments: List<Comment>,
    replies: Map<Int, List<Comment>>,
    commentLikeCounts: Map<Int, Int>,
    commentLikedByMe: Map<Int, Boolean>,
    replyToComment: Comment?,
    commentInput: String,
    currentUserId: Int,
    followingUserIds: Set<Int>,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onLikeComment: (Int) -> Unit,
    onReplyTo: (Comment) -> Unit,
    onCancelReply: () -> Unit,
    onFollowUser: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Text(
            text = "댓글 ${comments.size}개",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.heightIn(max = 320.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(comments) { comment ->
                CommentItem(
                    comment = comment,
                    likeCount = commentLikeCounts[comment.id] ?: 0,
                    isLiked = commentLikedByMe[comment.id] == true,
                    isFollowing = followingUserIds.contains(comment.authorId),
                    showFollowButton = comment.authorId != currentUserId,
                    onLike = { onLikeComment(comment.id) },
                    onReply = { onReplyTo(comment) },
                    onFollow = { onFollowUser(comment.authorId) },
                    isReply = false
                )
                replies[comment.id]?.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        likeCount = commentLikeCounts[reply.id] ?: 0,
                        isLiked = commentLikedByMe[reply.id] == true,
                        isFollowing = followingUserIds.contains(reply.authorId),
                        showFollowButton = reply.authorId != currentUserId,
                        onLike = { onLikeComment(reply.id) },
                        onReply = { onReplyTo(comment) },
                        onFollow = { onFollowUser(reply.authorId) },
                        isReply = true
                    )
                }
            }
        }

        HorizontalDivider()

        // 대댓글 대상 표시
        if (replyToComment != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("@${replyToComment.authorNickname} 에 답글 달기", fontSize = 13.sp, color = Color.Gray)
                TextButton(onClick = onCancelReply, contentPadding = PaddingValues(0.dp)) {
                    Text("취소", fontSize = 13.sp, color = GradientStart)
                }
            }
        }

        // 댓글 입력창
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentInput,
                onValueChange = onInputChange,
                placeholder = { Text(if (replyToComment != null) "답글 입력..." else "댓글 입력...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = GradientStart
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSubmit) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "전송", tint = GradientStart)
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    likeCount: Int,
    isLiked: Boolean,
    isFollowing: Boolean,
    showFollowButton: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onFollow: () -> Unit,
    isReply: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 48.dp else 16.dp, end = 16.dp, top = 10.dp, bottom = 4.dp)
    ) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = comment.authorNickname, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (showFollowButton) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFollowing) "팔로잉" else "팔로우",
                        fontSize = 11.sp,
                        color = if (isFollowing) GradientStart else Color.Gray,
                        modifier = Modifier.clickable(onClick = onFollow, indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = comment.body, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = comment.createdAt, fontSize = 11.sp, color = Color.Gray)
                if (!isReply) {
                    Text(
                        text = "답글 달기",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable(onClick = onReply, indication = null, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() })
                    )
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onLike, modifier = Modifier.size(32.dp)) {
                Icon(
                    if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) Color.Red else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (likeCount > 0) Text(text = "$likeCount", fontSize = 10.sp, color = Color.Gray)
        }
    }
}
