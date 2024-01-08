package com.example.powermanager.ui.model

import android.app.ActivityManager
import android.os.BatteryManager
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import com.example.powermanager.data.data_trackers.CPUFrequencyTracker
import com.example.powermanager.data.data_trackers.CPULoadTracker
import com.example.powermanager.data.data_trackers.MemoryLoadTracker
import com.example.powermanager.data.sampling.HomeScreenSamplingService
import com.example.powermanager.data.sampling.StatisticsScreenSamplingService
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.ui.state.HomeScreenInfo
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.NO_SCREEN
import com.example.powermanager.utils.STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME
import com.example.powermanager.utils.determineNumberOfCPUCores
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class AppModel(
    am: ActivityManager,
    pm: PowerManager,
    bm: BatteryManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float
    private val numberOfCores : Int

    private val activityManager : ActivityManager
    private val powerManager: PowerManager
    private val batteryManager: BatteryManager

    private var shutdownTimeForStatisticsSampling: Long
    private var previousScreenName : String = HOME_SCREEN_NAME

    init {
        activityManager = am
        powerManager = pm
        batteryManager = bm

        // determine the total amount of memory that the device has
        val info = ActivityManager.MemoryInfo()

        am.getMemoryInfo(info)
        totalMemory = getGigaBytesFromBytes(info.totalMem)

        // determine the number of processors on the device
        numberOfCores = determineNumberOfCPUCores()

        shutdownTimeForStatisticsSampling = 0L
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
                coreTracked = uiState.value.coreTracked,
                homeScreenInfo = uiState.value.homeScreenInfo
            )
        }

        if (newScreenName == HOME_SCREEN_NAME)
            HomeScreenSamplingService.startSampling(
                activityManager = activityManager,
                powerManager = powerManager,
                batteryManager = batteryManager,
                model = this
            )
    }

    fun changeTrackedCore(coreNumber: Int) {
        CPUFrequencyTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isSamplingForStatisticsScreen = uiState.value.isSamplingForStatisticsScreen,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = coreNumber,
                homeScreenInfo = uiState.value.homeScreenInfo
            )
        }
    }

    fun onLeaveApp() {
        previousScreenName = uiState.value.currentScreenName
        if (uiState.value.currentScreenName == STATISTICS_SCREEN_NAME)
            onLeaveStatisticsScreen()

        _uiState.update { currentState ->
            currentState.copy(
                currentScreenName = NO_SCREEN,
                isSamplingForStatisticsScreen = uiState.value.isSamplingForStatisticsScreen,
                coreTracked = uiState.value.coreTracked,
                homeScreenInfo = uiState.value.homeScreenInfo
            )
        }
    }

    fun onEnterApp() {
        changeAppScreen(previousScreenName)
    }

    private fun onEnterStatisticScreen() {
        // do not shutdown while on screen
        shutdownTimeForStatisticsSampling = 0L

        if (!uiState.value.isSamplingForStatisticsScreen) {
            // start the coroutine that samples memory usage
            StatisticsScreenSamplingService.startSampling(activityManager, this)

            _uiState.update { currentState ->
                currentState.copy(
                    isSamplingForStatisticsScreen = true,
                    currentScreenName = uiState.value.currentScreenName,
                    coreTracked = uiState.value.coreTracked,
                    homeScreenInfo = uiState.value.homeScreenInfo
                )
            }
        }
    }

    private fun onLeaveStatisticsScreen() {
        shutdownTimeForStatisticsSampling = Calendar.getInstance().time.time + STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS
    }

    fun shouldEndStatisticsSampling() : Boolean {
        if (shutdownTimeForStatisticsSampling == 0L)
            return false

        return Calendar.getInstance().time.time >= shutdownTimeForStatisticsSampling
    }

    fun endStatisticsSampling() {
        shutdownTimeForStatisticsSampling = 0L
        MemoryLoadTracker.clearValues()
        CPUFrequencyTracker.clearValues()
        CPULoadTracker.clearValues()

        _uiState.update { currentState ->
            currentState.copy(
                isSamplingForStatisticsScreen = false,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = uiState.value.coreTracked,
                homeScreenInfo = uiState.value.homeScreenInfo
            )
        }
    }

    fun saveHomeScreenInfo(info: HomeScreenInfo) {
        _uiState.update { currentState ->
            currentState.copy(
                isSamplingForStatisticsScreen = uiState.value.isSamplingForStatisticsScreen,
                currentScreenName = uiState.value.currentScreenName,
                coreTracked = uiState.value.coreTracked,
                homeScreenInfo = info
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