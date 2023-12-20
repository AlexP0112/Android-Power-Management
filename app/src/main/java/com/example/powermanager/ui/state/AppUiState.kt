package com.example.powermanager.ui.state

import com.example.powermanager.ui.navigation.HOME_SCREEN_NAME

data class AppUiState(
    val currentScreenName: String = HOME_SCREEN_NAME,
    val isRecordingMemoryInfo : Boolean = false,
    val coreTracked : Int = 0
)
