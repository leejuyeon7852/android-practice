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
    var isEditMode by mutableStateOf(false)
        private set

    private var editingPost: Post? = null

    fun initForEdit(post: Post) {
        editingPost = post
        title = post.title
        body = post.body
        imageUri = post.imageUri?.let { Uri.parse(it) }
        isEditMode = true
    }

    fun onTitleChange(value: String) { title = value }
    fun onBodyChange(value: String) { body = value }
    fun onImageSelected(uri: Uri?) { imageUri = uri }
    fun onCancelClick(onCancel: () -> Unit) {
        if (title.isNotBlank() || body.isNotBlank()) showCancelDialog = true
        else { resetState(); onCancel() }
    }
    fun onDismissDialog() { showCancelDialog = false }
    fun onConfirmCancel(onDone: () -> Unit) {
        resetState()
        showCancelDialog = false
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
            resetState()
            onSaved(postId.toInt())
        }
    }

    fun onUpdate(onDone: (postId: Int) -> Unit) {
        val original = editingPost ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            val updated = original.copy(
                title = title,
                body = body,
                imageUri = imageUri?.toString()
            )
            repository.updatePost(updated)
            resetState()
            onDone(original.id)
        }
    }

    private fun resetState() {
        title = ""; body = ""; imageUri = null; isEditMode = false; editingPost = null
    }
}
