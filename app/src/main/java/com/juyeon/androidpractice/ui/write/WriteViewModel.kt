package com.juyeon.androidpractice.ui.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class WriteViewModel : ViewModel() {
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
    fun onCancelClick() { showCancelDialog = true }
    fun onDismissDialog() { showCancelDialog = false }
    fun onConfirmCancel() {
        title = ""
        body = ""
        imageUri = null
        showCancelDialog = false
    }
    fun onSave() {
        // 나중에 Repository 연결
    }
}
