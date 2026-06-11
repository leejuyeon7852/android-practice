package com.juyeon.androidpractice.ui.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class WriteViewModel : ViewModel() {
    var title by mutableStateOf("")
        private set
    var body by mutableStateOf("")
        private set
    var showCancelDialog by mutableStateOf(false)
        private set

    fun onTitleChange(value: String) { title = value }
    fun onBodyChange(value: String) { body = value }
    fun onCancelClick() { showCancelDialog = true }
    fun onDismissDialog() { showCancelDialog = false }
    fun onConfirmCancel() {
        title = ""
        body = ""
        showCancelDialog = false
    }
    fun onSave() {
        // 나중에 Repository 연결
    }
}
