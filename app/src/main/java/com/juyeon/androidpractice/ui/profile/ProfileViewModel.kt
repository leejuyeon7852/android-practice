package com.juyeon.androidpractice.ui.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.dto.CommentResponse
import com.juyeon.androidpractice.data.network.dto.FollowUserResponse
import com.juyeon.androidpractice.data.network.dto.PostResponse
import com.juyeon.androidpractice.data.network.dto.toUser
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val api = ApiClient.api

    var myPosts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var myComments by mutableStateOf<List<CommentResponse>>(emptyList())
        private set
    var scrappedPosts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var likedPosts by mutableStateOf<List<PostResponse>>(emptyList())
        private set
    var followerUsers by mutableStateOf<List<FollowUserResponse>>(emptyList())
        private set
    var followingUsers by mutableStateOf<List<FollowUserResponse>>(emptyList())
        private set

    fun load() {
        viewModelScope.launch {
            try { myPosts = api.getMyPosts() } catch (_: Exception) {}
            try { myComments = api.getMyComments() } catch (_: Exception) {}
            try { scrappedPosts = api.getMyScraps() } catch (_: Exception) {}
            try { likedPosts = api.getMyLikes() } catch (_: Exception) {}
            try { followerUsers = api.getMyFollowers() } catch (_: Exception) {}
            try { followingUsers = api.getMyFollowing() } catch (_: Exception) {}
        }
    }

    fun updateProfile(
        context: Context,
        nickname: String?,
        imageUri: Uri?,
        onDone: (User) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val nicknamePart = nickname?.toRequestBody("text/plain".toMediaTypeOrNull())
                val imagePart = imageUri?.let { uri ->
                    if (uri.toString().startsWith("http")) null
                    else {
                        val inputStream = context.contentResolver.openInputStream(uri)!!
                        val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}")
                        FileOutputStream(file).use { it.write(inputStream.readBytes()) }
                        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("image", file.name, requestBody)
                    }
                }
                val updated = api.updateMe(nicknamePart, imagePart).toUser()
                onDone(updated)
            } catch (_: Exception) {}
        }
    }
}
