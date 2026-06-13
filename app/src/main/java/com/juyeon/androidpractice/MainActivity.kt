package com.juyeon.androidpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juyeon.androidpractice.ui.alarm.AlarmScreen
import com.juyeon.androidpractice.ui.auth.AuthViewModel
import com.juyeon.androidpractice.ui.auth.LoginScreen
import com.juyeon.androidpractice.ui.auth.SignupScreen
import com.juyeon.androidpractice.ui.theme.AndroidPracticeTheme
import com.juyeon.androidpractice.ui.theme.BackgroundBase
import com.juyeon.androidpractice.ui.component.BottomNavItem
import com.juyeon.androidpractice.ui.component.BottomNavigationBar
import com.juyeon.androidpractice.ui.component.GradientBackground
import com.juyeon.androidpractice.ui.home.HomeScreen
import com.juyeon.androidpractice.ui.post.PostDetailScreen
import com.juyeon.androidpractice.ui.profile.ProfileScreen
import com.juyeon.androidpractice.ui.search.SearchScreen
import com.juyeon.androidpractice.ui.write.WriteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPracticeTheme {
                val authViewModel: AuthViewModel = viewModel()
                var showSignup by remember { mutableStateOf(false) }
                var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }
                var selectedPostId by remember { mutableStateOf<Int?>(null) }

                val currentUser = authViewModel.currentUser

                if (currentUser == null) {
                    GradientBackground {
                        if (showSignup) {
                            SignupScreen(
                                onSignupSuccess = { showSignup = false },
                                onNavigateToLogin = { showSignup = false },
                                viewModel = authViewModel
                            )
                        } else {
                            LoginScreen(
                                onLoginSuccess = { /* currentUser가 세팅됨 */ },
                                onNavigateToSignup = { showSignup = true },
                                viewModel = authViewModel
                            )
                        }
                    }
                } else if (selectedPostId != null) {
                    // 게시글 상세보기 (전체화면)
                    GradientBackground {
                        PostDetailScreen(
                            postId = selectedPostId!!,
                            currentUser = currentUser,
                            onBack = { selectedPostId = null }
                        )
                    }
                } else {
                    Scaffold(
                        containerColor = BackgroundBase,
                        bottomBar = {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onItemClick = { currentRoute = it.route }
                            )
                        }
                    ) { innerPadding ->
                        GradientBackground {
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (currentRoute) {
                                    BottomNavItem.Home.route -> HomeScreen()
                                    BottomNavItem.Search.route -> SearchScreen(
                                        onPostClick = { selectedPostId = it }
                                    )
                                    BottomNavItem.Write.route -> WriteScreen(
                                        authorId = currentUser.id,
                                        authorNickname = currentUser.nickname,
                                        onSaved = { postId ->
                                            currentRoute = BottomNavItem.Search.route
                                            selectedPostId = postId
                                        },
                                        onCancel = { currentRoute = BottomNavItem.Home.route }
                                    )
                                    BottomNavItem.Profile.route -> ProfileScreen(
                                        nickname = currentUser.nickname
                                    )
                                    BottomNavItem.Alarm.route -> AlarmScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
