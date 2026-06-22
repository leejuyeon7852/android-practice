package com.juyeon.androidpractice.ui.write

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.PostResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class WriteViewModel : ViewModel() {

    private val api = ApiClient.api

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

    private var editingPost: PostResponse? = null

    fun initForEdit(post: PostResponse) {
        editingPost = post
        title = post.title
        body = post.body
        imageUri = post.imageUrl?.let { Uri.parse(it) }
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

    fun onSave(context: Context, onSaved: (post: PostResponse) -> Unit) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val imagePart = imageUri?.toMultipart(context, "image")
                val result = api.createPost(
                    title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    body = body.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart,
                )
                resetState()
                onSaved(result)
            } catch (_: Exception) {}
        }
    }

    fun onUpdate(context: Context, onDone: (post: PostResponse) -> Unit) {
        val original = editingPost ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val imagePart = imageUri?.let { uri ->
                    if (!uri.toString().startsWith("http")) uri.toMultipart(context, "image")
                    else null
                }
                val result = api.updatePost(
                    postId = original.id,
                    title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    body = body.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart,
                )
                resetState()
                onDone(result)
            } catch (_: Exception) {}
        }
    }

    private fun resetState() {
        title = ""; body = ""; imageUri = null; isEditMode = false; editingPost = null
    }
}

private fun Uri.toMultipart(context: Context, partName: String): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(this)!!
    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}")
    FileOutputStream(file).use { out -> inputStream.copyTo(out) }
    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, file.name, requestBody)
}
