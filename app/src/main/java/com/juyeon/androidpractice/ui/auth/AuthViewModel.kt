package com.juyeon.androidpractice.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.juyeon.androidpractice.model.User

class AuthViewModel : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set
    var userDraft by mutableStateOf(User(userId = "", password = "", nickname = "", email = ""))
        private set
    var loginError by mutableStateOf<String?>(null)
        private set
    var signupError by mutableStateOf<String?>(null)
        private set

    // Mock 저장소 — 나중에 RoomDB 또는 Remote Repository로 교체
    private val mockUsers = mutableListOf<User>()

    fun login(id: String, password: String, onSuccess: () -> Unit) {
        if (id.isBlank() || password.isBlank()) {
            loginError = "아이디와 비밀번호를 입력해주세요"
            return
        }
        val found = mockUsers.find { it.userId == id && it.password == password }
        if (found != null) {
            currentUser = found
            loginError = null
            onSuccess()
        } else {
            loginError = "아이디 또는 비밀번호가 올바르지 않습니다"
        }
    }

    fun signup(
        user: User,
        password: String,
        passwordConfirm: String,
        onSuccess: () -> Unit
    ) {
        val idRegex = Regex("^[a-zA-Z0-9]{4,}$")
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$")
        when {
            user.nickname.isBlank() || user.email.isBlank() || user.userId.isBlank() || password.isBlank() -> {
                signupError = "모든 필수 항목을 입력해주세요"
            }
            !idRegex.matches(user.userId) -> {
                signupError = "아이디는 영문·숫자 4자 이상이어야 합니다"
            }
            !passwordRegex.matches(password) -> {
                signupError = "비밀번호는 영문·숫자 포함 8자 이상이어야 합니다"
            }
            password != passwordConfirm -> {
                signupError = "비밀번호가 일치하지 않습니다"
            }
            mockUsers.any { it.userId == user.userId } -> {
                signupError = "이미 사용 중인 아이디입니다"
            }
            else -> {
                mockUsers.add(user)
                signupError = null
                onSuccess()
            }
        }
    }

    fun updateDraft(update: User.() -> User) {
        userDraft = userDraft.update()
        signupError = null
    }

    fun clearLoginError() { loginError = null }
    fun clearSignupError() { signupError = null }
}
