package com.example.powermanager.ui.state

import com.example.powermanager.utils.HOME_SCREEN_NAME
import java.time.Duration

data class HomeScreenInfo(
    val isBatteryCharging : Boolean = false,
    val currentBatteryLevel : Int = 0,
    val chargeOrDischargePrediction: Duration? = null
)

data class AppUiState(
    val currentScreenName: String = HOME_SCREEN_NAME,
    val isSamplingForStatisticsScreen : Boolean = false,
    val coreTracked : Int = 0,
    val homeScreenInfo: HomeScreenInfo = HomeScreenInfo()
)
