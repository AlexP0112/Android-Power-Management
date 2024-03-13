package com.example.powermanager.ui.state

import com.example.powermanager.utils.NO_VALUE_STRING

data class RecordingScreensUiState(
    val isRecording : Boolean = false,
    val recordingResults : List<String>,
    val currentlySelectedRecordingResult : String = NO_VALUE_STRING,
    val selectedToCompareRecordingResult : String = NO_VALUE_STRING
)
