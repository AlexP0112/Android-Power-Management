package com.example.powermanager.ui.navigation

import com.example.powermanager.R
import com.example.powermanager.utils.CONTROL_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.LIVE_CHARTS_SCREEN_NAME
import com.example.powermanager.utils.RECORDING_SCREEN_NAME
import com.example.powermanager.utils.SETTINGS_SCREEN_NAME

data class NavigationItem(
    val title: String,
    val iconId: Int,
)

val navigationItems = listOf(
    NavigationItem(
        title = HOME_SCREEN_NAME,
        iconId = R.drawable.home_svgrepo_com,
    ),

    NavigationItem(
        title = LIVE_CHARTS_SCREEN_NAME,
        iconId = R.drawable.chart_line_up_svgrepo_com,
    ),

    NavigationItem(
        title = RECORDING_SCREEN_NAME,
        iconId = R.drawable.record_video_stop_svgrepo_com
    ),

    NavigationItem(
        title = CONTROL_SCREEN_NAME,
        iconId = R.drawable.controls_svgrepo_com,
    ),

    NavigationItem(
        title = SETTINGS_SCREEN_NAME,
        iconId = R.drawable.settings_svgrepo_com
    )
)
