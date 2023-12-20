package com.example.powermanager.ui.model

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.powermanager.data.sampling.CPUFrequencyTracker
import com.example.powermanager.data.sampling.MemoryLoadTracker
import com.example.powermanager.data.sampling.SamplingService
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.determineNumberOfCPUCores
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar


const val BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 100L * 1000L // 1min 40s

class AppModel(applicationContext: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float
    private val numberOfCores : Int

    private var shutdownTimeForSampling: Long


    init {
        // determine the total amount of memory that the device has
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
        val info = ActivityManager.MemoryInfo()

        (am as ActivityManager).getMemoryInfo(info)
        totalMemory = getGigaBytesFromBytes(info.totalMem)

        // determine the number of processors on the device
        numberOfCores = determineNumberOfCPUCores()

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
                isRecordingMemoryInfo = uiState.value.isRecordingMemoryInfo,
                coreTracked = uiState.value.coreTracked
            )
        }
    }

    fun changeTrackedCore(coreNumber: Int) {
        CPUFrequencyTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isRecordingMemoryInfo = uiState.value.isRecordingMemoryInfo,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = coreNumber
            )
        }
    }

    fun onEnterStatisticScreen(context: Context) {
        // do not shutdown while on screen
        shutdownTimeForSampling = 0L

        if (!uiState.value.isRecordingMemoryInfo) {
            // start the coroutine that samples memory usage
            SamplingService.startSampling(context, this)

            _uiState.update { currentState ->
                currentState.copy(
                    isRecordingMemoryInfo = true,
                    currentScreenName = uiState.value.currentScreenName,
                    coreTracked = uiState.value.coreTracked
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
        CPUFrequencyTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isRecordingMemoryInfo = false,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = uiState.value.coreTracked
            )
        }
    }

    fun getTotalMemory(): Float {
        return totalMemory
    }

    fun getNumCores(): Int {
        return numberOfCores
    }

}