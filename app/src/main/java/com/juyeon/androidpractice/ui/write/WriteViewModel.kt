package com.juyeon.androidpractice.ui.write

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.Post
import com.juyeon.androidpractice.data.repository.post.PostRepositoryImpl
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepositoryImpl(AppDatabase.getInstance(application).postDao())

    var title by mutableStateOf("")
        private set
    var body by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set
    var showCancelDialog by mutableStateOf(false)
        private set

    fun onTitleChange(value: String) { title = value }
    fun onBodyChange(value: String) { body = value }
    fun onImageSelected(uri: Uri?) { imageUri = uri }
    fun onCancelClick() { if (title.isNotBlank() || body.isNotBlank()) showCancelDialog = true }
    fun onDismissDialog() { showCancelDialog = false }
    fun onConfirmCancel(onDone: () -> Unit) {
        title = ""; body = ""; imageUri = null; showCancelDialog = false
        onDone()
    }

    fun onSave(authorId: Int, authorNickname: String, onSaved: (postId: Int) -> Unit) {
        if (title.isBlank()) return
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        viewModelScope.launch {
            val postId = repository.savePost(
                Post(
                    authorId = authorId,
                    authorNickname = authorNickname,
                    title = title,
                    body = body,
                    imageUri = imageUri?.toString(),
                    createdAt = now,
                )
            )
            title = ""; body = ""; imageUri = null
            onSaved(postId.toInt())
        }
    }
}
