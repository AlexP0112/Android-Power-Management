package com.example.powermanager.ui.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.utils.DEFAULT_RECORDING_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS
import com.example.powermanager.utils.NO_VALUE_STRING

data class RecordingScreensUiState(
    val isRecording : Boolean = false,
    val recordingSamplingPeriod : Long = DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS,
    val recordingNumberOfSamplesString : String = DEFAULT_RECORDING_NUMBER_OF_SAMPLES.toString(),
    val recordingSessionName : String = "",
    val recordingResults : List<String>,
    val currentlySelectedRecordingResult : String = NO_VALUE_STRING,
    val includeThreadCountInfo : Boolean = true,
    val infoDialogTextId : Int? = null,
    val infoDialogHeightDp: Dp = 0.dp,
    val isConfirmDeletionDialogOpen : Boolean = false,
    val isInspectFileDialogOpen : Boolean = false,
    val isSamplingPeriodDropdownExpanded : Boolean = false
)
