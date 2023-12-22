package com.example.powermanager.ui.model

import android.app.ActivityManager
import androidx.lifecycle.ViewModel
import com.example.powermanager.data.data_trackers.CPUFrequencyTracker
import com.example.powermanager.data.data_trackers.CPULoadTracker
import com.example.powermanager.data.data_trackers.MemoryLoadTracker
import com.example.powermanager.data.sampling.StatisticsScreenSamplingService
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.BACKGROUND_SAMPLING_THRESHOLD_MILLIS
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME
import com.example.powermanager.utils.determineNumberOfCPUCores
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class AppModel(am: ActivityManager) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float
    private val numberOfCores : Int

    private val activityManager : ActivityManager
    private var shutdownTimeForSampling: Long

    init {
        activityManager = am

        // determine the total amount of memory that the device has
        val info = ActivityManager.MemoryInfo()

        am.getMemoryInfo(info)
        totalMemory = getGigaBytesFromBytes(info.totalMem)

        // determine the number of processors on the device
        numberOfCores = determineNumberOfCPUCores()

        shutdownTimeForSampling = 0L
    }

    fun changeAppScreen(newScreenName: String) {
        if (uiState.value.currentScreenName == STATISTICS_SCREEN_NAME)
            onLeaveStatisticsScreen()

        if (newScreenName == STATISTICS_SCREEN_NAME)
            onEnterStatisticScreen()

        _uiState.update { currentState ->
            currentState.copy(
                currentScreenName = newScreenName,
                isSamplingForStatisticsScreen = uiState.value.isSamplingForStatisticsScreen,
                coreTracked = uiState.value.coreTracked
            )
        }
    }

    fun changeTrackedCore(coreNumber: Int) {
        CPUFrequencyTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isSamplingForStatisticsScreen = uiState.value.isSamplingForStatisticsScreen,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = coreNumber
            )
        }
    }

    fun onEnterStatisticScreen() {
        // do not shutdown while on screen
        shutdownTimeForSampling = 0L

        if (!uiState.value.isSamplingForStatisticsScreen) {
            // start the coroutine that samples memory usage
            StatisticsScreenSamplingService.startSampling(activityManager, this)

            _uiState.update { currentState ->
                currentState.copy(
                    isSamplingForStatisticsScreen = true,
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
        CPULoadTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isSamplingForStatisticsScreen = false,
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