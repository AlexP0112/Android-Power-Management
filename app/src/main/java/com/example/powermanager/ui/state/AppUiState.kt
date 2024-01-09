package com.example.powermanager.ui.state

import com.example.powermanager.utils.HOME_SCREEN_NAME
import java.time.Duration

data class AppUiState(
    val currentScreenName: String = HOME_SCREEN_NAME,
    val isSamplingForStatisticsScreen : Boolean = false,
    val coreTracked : Int = 0,
)
