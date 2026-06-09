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
import com.juyeon.androidpractice.ui.alarm.AlarmScreen
import com.juyeon.androidpractice.ui.theme.AndroidPracticeTheme
import com.juyeon.androidpractice.ui.theme.BackgroundBase
import com.juyeon.androidpractice.ui.component.BottomNavItem
import com.juyeon.androidpractice.ui.component.BottomNavigationBar
import com.juyeon.androidpractice.ui.component.GradientBackground
import com.juyeon.androidpractice.ui.home.HomeScreen
import com.juyeon.androidpractice.ui.profile.ProfileScreen
import com.juyeon.androidpractice.ui.search.SearchScreen
import com.juyeon.androidpractice.ui.write.WriteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPracticeTheme {
                var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }

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
                        Box(modifier = Modifier.padding(innerPadding)){
                            when(currentRoute){
                                BottomNavItem.Home.route -> HomeScreen()
                                BottomNavItem.Search.route -> SearchScreen()
                                BottomNavItem.Write.route -> WriteScreen()
                                BottomNavItem.Profile.route -> ProfileScreen()
                                BottomNavItem.Alarm.route -> AlarmScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}