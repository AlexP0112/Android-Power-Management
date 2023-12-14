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

class AppModel(applicationContext: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float

    init {
        // determine the amount of the memory that the device has
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
        val info = ActivityManager.MemoryInfo()

        (am as ActivityManager).getMemoryInfo(info)
        totalMemory = getGigaBytesFromBytes(info.totalMem)
    }

    fun changeAppScreen(newScreenName: String, context: Context) {
        _uiState.update { currentState ->
            currentState.copy(
                currentScreenName = newScreenName,
                isRecordingMemoryInfo = uiState.value.isRecordingMemoryInfo
            )
        }

        if (newScreenName == STATISTICS_SCREEN_NAME)
            onEnterStatisticScreen(context)
    }

    private fun onEnterStatisticScreen(context: Context) {
        if (!uiState.value.isRecordingMemoryInfo) {
            // start the coroutine that samples memory usage
            MemoryService.startSampling(context)

            _uiState.update { currentState ->
                currentState.copy(
                    isRecordingMemoryInfo = true,
                    currentScreenName = uiState.value.currentScreenName
                )
            }
        }
    }

    fun getTotalMemory(): Float {
        return totalMemory
    }

}