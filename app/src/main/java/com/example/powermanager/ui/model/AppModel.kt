package com.example.powermanager.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

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

}