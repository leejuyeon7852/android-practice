package com.juyeon.androidpractice.ui.theme.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.juyeon.androidpractice.ui.theme.BackgroundBase
import com.juyeon.androidpractice.ui.theme.GradientStart

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
){
    object Home: BottomNavItem("home", "홈", Icons.Filled.Home)
    object Search: BottomNavItem("search", "탐색", Icons.Filled.Search)
    object Write: BottomNavItem("write", "글쓰기", Icons.Filled.Edit)
    object Alarm: BottomNavItem("alarm", "알람", Icons.Filled.Notifications)
    object Profile: BottomNavItem("profile", "MY", Icons.Filled.Person)
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
){
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Write,
        BottomNavItem.Alarm,
        BottomNavItem.Profile,
    )

    NavigationBar(
        containerColor = BackgroundBase,
        windowInsets = WindowInsets(0)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GradientStart,
                    selectedTextColor = GradientStart,
                    indicatorColor = Color(0xFFB8D4E3),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview(){
    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = "home", onItemClick = {})
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
}