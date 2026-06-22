package com.juyeon.androidpractice.ui.write

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.juyeon.androidpractice.ui.theme.GradientStart
import java.io.File

@Composable
fun WriteScreen(
    authorId: Int,
    authorNickname: String,
    onSaved: (post: com.juyeon.androidpractice.data.network.dto.PostResponse) -> Unit,
    onCancel: () -> Unit,
    editPost: com.juyeon.androidpractice.data.network.dto.PostResponse? = null,
    viewModel: WriteViewModel = viewModel()
) {
    LaunchedEffect(editPost?.id) {
        if (editPost != null) viewModel.initForEdit(editPost)
    }
    val context = LocalContext.current
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
        }
        viewModel.onImageSelected(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) viewModel.onImageSelected(cameraImageUri) }

    if (viewModel.showCancelDialog) {
        CancelDialog(
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmCancel(onCancel) }
        )
    }

    if (showImagePickerDialog) {
        ImageSourceDialog(
            onGallery = {
                showImagePickerDialog = false
                galleryLauncher.launch("image/*")
            },
            onCamera = {
                showImagePickerDialog = false
                val imageFile = File(context.externalCacheDir, "camera_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            },
            onDismiss = { showImagePickerDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.onTitleChange(it) },
            placeholder = { Text("제목을 입력하세요") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.body,
            onValueChange = { viewModel.onBodyChange(it) },
            placeholder = { Text("내용을 입력하세요") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 이미지 첨부 버튼
        OutlinedButton(
            onClick = { showImagePickerDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("사진 첨부")
        }

        // 선택된 이미지 미리보기
        viewModel.imageUri?.let { uri ->
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                )
                IconButton(
                    onClick = { viewModel.onImageSelected(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (viewModel.isEditMode) viewModel.onConfirmCancel(onCancel)
                    else viewModel.onCancelClick(onCancel)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("취소")
            }

            Button(
                onClick = {
                    if (viewModel.isEditMode) viewModel.onUpdate(context, onSaved)
                    else viewModel.onSave(context, onSaved)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (viewModel.isEditMode) "수정 완료" else "저장", color = Color.White)
            }
        }
    }
}

@Composable
private fun ImageSourceDialog(
    onGallery: () -> Unit,
    onCamera: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("사진 선택", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onGallery,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("갤러리에서 선택", color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCamera,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("지금 사진 찍기")
                }
            }
        }
    }
}

@Composable
private fun CancelDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("취소하시겠습니까?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("작성 중인 내용이 사라집니다.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("아니오")
                    }
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("확인", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WriteScreenPreview() {
    WriteScreen(authorId = 0, authorNickname = "미리보기", onSaved = {}, onCancel = {})
}
