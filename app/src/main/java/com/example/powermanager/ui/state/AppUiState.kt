package com.example.powermanager.ui.state

import com.example.powermanager.utils.HOME_SCREEN_NAME

data class AppUiState(
    val currentScreenName: String = HOME_SCREEN_NAME,
    val isSamplingForStatisticsScreen : Boolean = false,
    val coreTracked : Int = 0
)
