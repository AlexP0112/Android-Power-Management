package com.example.powermanager.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

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
            // start the worker that tracks memory usage
            val constraints = Constraints.Builder().build()
            val workRequest = OneTimeWorkRequestBuilder<MemoryWorker>()
                .setConstraints(constraints)
                .setInputData(Data.EMPTY)
                .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).beginUniqueWork("Memory sampling work", ExistingWorkPolicy.KEEP, workRequest).enqueue()
            _uiState.update { currentState ->
                currentState.copy(
                    isRecordingMemoryInfo = true,
                    currentScreenName = uiState.value.currentScreenName
                )
            }
        }
    }

}