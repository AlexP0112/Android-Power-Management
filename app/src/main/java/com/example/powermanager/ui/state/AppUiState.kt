package com.example.powermanager.ui.state

import com.example.powermanager.utils.DEFAULT_RECORDING_NAME
import com.example.powermanager.utils.DEFAULT_RECORDING_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS

data class AppUiState(
    val coreTracked : Int = 0,
    val isRecording : Boolean = false,
    val recordingSamplingPeriod : Long = DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS,
    val recordingNumberOfSamplesString : String = DEFAULT_RECORDING_NUMBER_OF_SAMPLES.toString(),
    val recordingSessionName : String = DEFAULT_RECORDING_NAME
)
