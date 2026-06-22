package com.juyeon.androidpractice.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.network.ApiClient
import com.juyeon.androidpractice.data.network.TokenManager
import com.juyeon.androidpractice.data.network.dto.LoginRequest
import com.juyeon.androidpractice.data.network.dto.SignupRequest
import com.juyeon.androidpractice.data.network.dto.toUser
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val api = ApiClient.api.also { ApiClient.init(tokenManager) }

    var currentUser by mutableStateOf<User?>(null)
        private set
    var userDraft by mutableStateOf(User())
        private set
    var loginError by mutableStateOf<String?>(null)
        private set
    var signupError by mutableStateOf<String?>(null)
        private set

    init {
        if (tokenManager.get() != null) {
            viewModelScope.launch {
                try {
                    currentUser = api.getMe().toUser()
                } catch (e: Exception) {
                    tokenManager.clear()
                }
            }
        }
    }

    fun login(id: String, password: String, onSuccess: () -> Unit) {
        if (id.isBlank() || password.isBlank()) {
            loginError = "아이디와 비밀번호를 입력해주세요"
            return
        }
        viewModelScope.launch {
            try {
                val token = api.login(LoginRequest(id, password))
                tokenManager.save(token.accessToken)
                currentUser = api.getMe().toUser()
                loginError = null
                onSuccess()
            } catch (e: Exception) {
                loginError = "아이디 또는 비밀번호가 올바르지 않습니다"
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        currentUser = null
        userDraft = User()
    }

    fun signup(user: User, password: String, passwordConfirm: String, onSuccess: () -> Unit) {
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
            else -> {
                viewModelScope.launch {
                    try {
                        api.signup(SignupRequest(user.userId, user.nickname, user.email, password))
                        signupError = null
                        onSuccess()
                    } catch (e: retrofit2.HttpException) {
                        signupError = if (e.code() == 400) "이미 사용 중인 아이디입니다" else "오류가 발생했습니다"
                    }
                }
            }
        }
    }

    fun updateDraft(update: User.() -> User) {
        userDraft = userDraft.update()
        signupError = null
    }

    fun updateUser(user: User) {
        currentUser = user
    }

    fun clearLoginError() { loginError = null }
    fun clearSignupError() { signupError = null }
}
