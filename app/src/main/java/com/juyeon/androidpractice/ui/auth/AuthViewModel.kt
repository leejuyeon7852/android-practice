package com.juyeon.androidpractice.ui.auth

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juyeon.androidpractice.data.db.AppDatabase
import com.juyeon.androidpractice.data.db.entity.User
import com.juyeon.androidpractice.data.repository.auth.AuthRepositoryImpl
import kotlinx.coroutines.launch

private const val PREF_NAME = "auth_pref"
private const val KEY_USER_ID = "logged_in_user_id"

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val db = AppDatabase.getInstance(application)
    private val repository = AuthRepositoryImpl(db.userDao())

    var currentUser by mutableStateOf<User?>(null)
        private set
    var userDraft by mutableStateOf(User(userId = "", password = "", nickname = "", email = ""))
        private set
    var loginError by mutableStateOf<String?>(null)
        private set
    var signupError by mutableStateOf<String?>(null)
        private set

    init {
        // 앱 시작 시 저장된 유저 ID로 자동 로그인
        val savedId = prefs.getInt(KEY_USER_ID, -1)
        if (savedId != -1) {
            viewModelScope.launch {
                currentUser = db.userDao().findById(savedId)
            }
        }
    }

    fun login(id: String, password: String, onSuccess: () -> Unit) {
        if (id.isBlank() || password.isBlank()) {
            loginError = "아이디와 비밀번호를 입력해주세요"
            return
        }
        viewModelScope.launch {
            val user = repository.login(id, password)
            if (user != null) {
                currentUser = user
                prefs.edit().putInt(KEY_USER_ID, user.id).apply()
                loginError = null
                onSuccess()
            } else {
                loginError = "아이디 또는 비밀번호가 올바르지 않습니다"
            }
        }
    }

    fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
        currentUser = null
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
            else -> {
                viewModelScope.launch {
                    val success = repository.signup(user)
                    if (success) {
                        signupError = null
                        onSuccess()
                    } else {
                        signupError = "이미 사용 중인 아이디입니다"
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
        viewModelScope.launch {
            repository.updateUser(user)
            currentUser = user
        }
    }

    fun clearLoginError() { loginError = null }
    fun clearSignupError() { signupError = null }
}
