package com.example.powermanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
)

const val HOME_SCREEN_NAME = "Home";
const val STATISTICS_SCREEN_NAME = "Statistics";
const val CONTROL_SCREEN_NAME = "Control"

val navigationItems = listOf(
    NavigationItem(
        title = HOME_SCREEN_NAME,
        icon = Icons.Filled.Home,
    ),

    NavigationItem(
        title = STATISTICS_SCREEN_NAME,
        icon = Icons.Filled.Info,
    ),

    NavigationItem(
        title = CONTROL_SCREEN_NAME,
        icon = Icons.Filled.Build,
    ),
)
