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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.network.dto.CommentResponse
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Int,
    currentUser: User,
    onBack: () -> Unit,
    onEdit: (PostResponse) -> Unit = {},
    onUserClick: (userId: Int) -> Unit = {},
    viewModel: PostDetailViewModel = viewModel()
) {
    LaunchedEffect(postId) { viewModel.init(postId, currentUser.id, currentUser.nickname) }

    val post = viewModel.post
    val comments = viewModel.comments
    var showCommentSheet by remember { mutableStateOf(false) }
    var showPostMenu by remember { mutableStateOf(false) }
    var showDeletePostDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePostDialog = false },
            title = { Text("게시글 삭제") },
            text = { Text("정말 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeletePostDialog = false
                    viewModel.deletePost { onBack() }
                }) { Text("삭제", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostDialog = false }) { Text("취소") }
            }
        )
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
                actions = {
                    if (post != null && post.authorId == currentUser.id) {
                        Box {
                            IconButton(onClick = { showPostMenu = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "더보기")
                            }
                            DropdownMenu(
                                expanded = showPostMenu,
                                onDismissRequest = { showPostMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("수정") },
                                    onClick = { showPostMenu = false; onEdit(post) }
                                )
                                DropdownMenuItem(
                                    text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                                    onClick = { showPostMenu = false; showDeletePostDialog = true }
                                )
                            }
                        }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = { onUserClick(post.authorId) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                        ) {
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.LightGray))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = post.authorNickname, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = post.createdAt.take(10), fontSize = 12.sp, color = Color.Gray)
                        if (post.authorId != currentUser.id) {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { viewModel.toggleFollow() },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.isFollowing) Color.LightGray else GradientStart
                                ),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (viewModel.isFollowing) "팔로잉" else "팔로우",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (post.imageUrl != null) {
                        AsyncImage(
                            model = ApiClient.imageUrl(post.imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(text = post.body, fontSize = 16.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleLike() }) {
                            Icon(
                                if (viewModel.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (viewModel.isLiked) Color.Red else Color.Gray
                            )
                        }
                        Text("${viewModel.likeCount}", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.toggleScrap() }) {
                            Icon(
                                if (viewModel.isScrapped) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "스크랩",
                                tint = if (viewModel.isScrapped) GradientStart else Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
                commentLikeCounts = viewModel.commentLikeCounts,
                commentLikedByMe = viewModel.commentLikedByMe,
                replyToComment = viewModel.replyToComment,
                commentInput = viewModel.commentInput,
                currentUserId = currentUser.id,
                onInputChange = viewModel::onCommentInputChange,
                onSubmit = { viewModel.submitComment() },
                onLikeComment = { viewModel.toggleCommentLike(it) },
                onReplyTo = { viewModel.setReplyTo(it) },
                onCancelReply = { viewModel.setReplyTo(null) },
                onDeleteComment = { viewModel.deleteComment(it) },
                onUserClick = onUserClick,
            )
        }
    }
}

@Composable
private fun CommentSheetContent(
    comments: List<CommentResponse>,
    commentLikeCounts: Map<Int, Int>,
    commentLikedByMe: Map<Int, Boolean>,
    replyToComment: CommentResponse?,
    commentInput: String,
    currentUserId: Int,
    onInputChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onLikeComment: (Int) -> Unit,
    onReplyTo: (CommentResponse) -> Unit,
    onCancelReply: () -> Unit,
    onDeleteComment: (Int) -> Unit = {},
    onUserClick: (Int) -> Unit = {},
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
                    isReply = false,
                    showDeleteButton = comment.authorId == currentUserId,
                    onLike = { onLikeComment(comment.id) },
                    onReply = { onReplyTo(comment) },
                    onDelete = { onDeleteComment(comment.id) },
                    onUserClick = { onUserClick(comment.authorId) }
                )
                comment.replies.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        likeCount = commentLikeCounts[reply.id] ?: 0,
                        isLiked = commentLikedByMe[reply.id] == true,
                        isReply = true,
                        showDeleteButton = reply.authorId == currentUserId,
                        onLike = { onLikeComment(reply.id) },
                        onReply = { onReplyTo(comment) },
                        onDelete = { onDeleteComment(reply.id) },
                        onUserClick = { onUserClick(reply.authorId) }
                    )
                }
            }
        }

        HorizontalDivider()

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
    comment: CommentResponse,
    likeCount: Int,
    isLiked: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    isReply: Boolean,
    showDeleteButton: Boolean = false,
    onDelete: () -> Unit = {},
    onUserClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 48.dp else 16.dp, end = 16.dp, top = 10.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)
                .clickable(onClick = onUserClick, indication = null, interactionSource = remember { MutableInteractionSource() })
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.authorNickname,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onUserClick, indication = null, interactionSource = remember { MutableInteractionSource() })
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = comment.body, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = comment.createdAt.take(10), fontSize = 11.sp, color = Color.Gray)
                if (!isReply) {
                    Text(
                        text = "답글 달기",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable(onClick = onReply, indication = null, interactionSource = remember { MutableInteractionSource() })
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
            if (showDeleteButton) {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "삭제",
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
