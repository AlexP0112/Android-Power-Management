package com.example.powermanager.ui.model

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

const val BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 3L * 60L * 1000L // 3 minutes

class AppModel(applicationContext: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float

    private var shutdownTimeForSampling: Long

    init {
        // determine the amount of the memory that the device has
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
        val info = ActivityManager.MemoryInfo()

        (am as ActivityManager).getMemoryInfo(info)
        totalMemory = getGigaBytesFromBytes(info.totalMem)

        shutdownTimeForSampling = 0L
    }

    fun changeAppScreen(newScreenName: String, context: Context) {
        if (uiState.value.currentScreenName == STATISTICS_SCREEN_NAME)
            onLeaveStatisticsScreen()

        if (newScreenName == STATISTICS_SCREEN_NAME)
            onEnterStatisticScreen(context)

        _uiState.update { currentState ->
            currentState.copy(
                currentScreenName = newScreenName,
                isRecordingMemoryInfo = uiState.value.isRecordingMemoryInfo
            )
        }
    }

    fun onEnterStatisticScreen(context: Context) {
        if (!uiState.value.isRecordingMemoryInfo) {
            // start the coroutine that samples memory usage
            MemoryService.startSampling(context, this)

            _uiState.update { currentState ->
                currentState.copy(
                    isRecordingMemoryInfo = true,
                    currentScreenName = uiState.value.currentScreenName
                )
            }
        }
    }

    fun onLeaveStatisticsScreen() {
        shutdownTimeForSampling = Calendar.getInstance().time.time + BACKGROUND_SAMPLING_THRESHOLD_MILLIS
    }

    fun shouldEndSampling() : Boolean {
        if (shutdownTimeForSampling == 0L)
            return false

        return Calendar.getInstance().time.time >= shutdownTimeForSampling
    }

    fun endSampling() {
        shutdownTimeForSampling = 0L
        MemoryLoadTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isRecordingMemoryInfo = false,
                currentScreenName = uiState.value.currentScreenName
            )
        }
    }

    fun getTotalMemory(): Float {
        return totalMemory
    }

}