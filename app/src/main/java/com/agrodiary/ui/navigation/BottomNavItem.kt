package com.agrodiary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Главная",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Animals : BottomNavItem(
        route = Screen.Animals.route,
        title = "Животные",
        selectedIcon = Icons.Filled.Pets,
        unselectedIcon = Icons.Outlined.Pets
    )

    object Journal : BottomNavItem(
        route = Screen.Journal.route,
        title = "Журнал",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    )

    companion object {
        val items = listOf(Home, Animals, Journal)
    }
}
