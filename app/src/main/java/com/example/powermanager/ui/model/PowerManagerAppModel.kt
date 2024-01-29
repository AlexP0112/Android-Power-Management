package com.example.powermanager.ui.model

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.preferences.PreferencesManager
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.CORE_FREQUENCY_PATH
import com.example.powermanager.utils.FAILED_TO_DETERMINE
import com.example.powermanager.utils.HOME_SCREEN_SAMPLING_RATE_MILLIS
import com.example.powermanager.utils.NUMBER_OF_VALUES_TRACKED
import com.example.powermanager.utils.STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS
import com.example.powermanager.utils.STATISTICS_SCREEN_SAMPLING_RATE_MILLIS
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.convertBytesToGigaBytes
import com.example.powermanager.utils.convertKHzToGHz
import com.example.powermanager.utils.convertMicroAmpsToMilliAmps
import com.example.powermanager.utils.determineNumberOfCPUCores
import com.example.powermanager.utils.determineSystemBootTimestamp
import com.example.powermanager.utils.formatDuration
import com.example.powermanager.utils.getLoadAverageFromUptimeCommandOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.time.Duration
import java.util.Calendar

class PowerManagerAppModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val totalMemory: Float
    private val numberOfCores : Int
    private val systemBootTimestamp : Long

    private val activityManager : ActivityManager
    private val powerManager: PowerManager
    private val batteryManager: BatteryManager
    private val preferencesManager: PreferencesManager

    private var memoryUsageSamples: MutableList<FlowSample> = mutableListOf()
    private var cpuFrequencySamples: MutableList<FlowSample> = mutableListOf()
    private var cpuLoadSamples: MutableList<FlowSample> = mutableListOf()

    init {
        activityManager = application.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        powerManager = application.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        batteryManager = application.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        preferencesManager = PreferencesManager(application.applicationContext)

        // determine the total amount of memory that the device has
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        totalMemory = convertBytesToGigaBytes(info.totalMem)

        // determine the number of processors on the device and the timestamp of the system boot
        numberOfCores = determineNumberOfCPUCores()

        // determine the timestamp of the last system boot
        systemBootTimestamp =
            try {
                determineSystemBootTimestamp()
            } catch (e: Exception) {
                0L
            }

        // initialize the user preferences
        viewModelScope.launch {
            preferencesManager.initializePreferences()
        }
    }

    // sampling for home screen

    @SuppressLint("NewApi")
    val homeScreenInfoFlow = flow {
        while (true) {
            // battery and uptime
            val currentBatteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            val batteryChargeCountMilliAmps = convertMicroAmpsToMilliAmps(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))

            val chargeOrDischargePrediction: Duration? = if (!batteryManager.isCharging) {
                powerManager.batteryDischargePrediction
            } else {
                val chargeTimeRemainingMillis = batteryManager.computeChargeTimeRemaining()
                if (chargeTimeRemainingMillis == -1L) null else Duration.ofMillis(chargeTimeRemainingMillis)
            }
            val uptimeString = if (systemBootTimestamp != 0L)
                            formatDuration(Duration.ofMillis(Calendar.getInstance().timeInMillis - systemBootTimestamp))
                            else FAILED_TO_DETERMINE

            // memory usage info
            val info = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(info)
            val usedMemory = info.totalMem - info.availMem
            val usedMemoryGB = convertBytesToGigaBytes(usedMemory)

            // cpu info
            val cpuFrequenciesGHz : List<Float> = (0 until numberOfCores).map { core ->
                val path = String.format(CORE_FREQUENCY_PATH, core)
                val frequencyKHz = File(path).readText().trim().toInt()
                convertKHzToGHz(frequencyKHz)
            }

            val uptimeProcess = Runtime.getRuntime().exec(UPTIME_COMMAND)
            val uptimeOutput = BufferedReader(InputStreamReader(uptimeProcess.inputStream)).readText()
            uptimeProcess.waitFor()
            val load = getLoadAverageFromUptimeCommandOutput(uptimeOutput, LoadAverageTypes.LAST_MINUTE)

            // emit a single object that contains all the information
            emit(
                HomeScreenInfo(
                    isBatteryCharging = batteryManager.isCharging,
                    currentBatteryLevel = currentBatteryLevel,
                    batteryChargeCount = batteryChargeCountMilliAmps,
                    chargeOrDischargePrediction = chargeOrDischargePrediction,
                    powerSaveState = powerManager.isPowerSaveMode,
                    usedMemoryGB = usedMemoryGB,
                    cpuFrequenciesGHz = cpuFrequenciesGHz,
                    cpuLoad = load,
                    systemUptimeString = uptimeString
                )
            )

            delay(HOME_SCREEN_SAMPLING_RATE_MILLIS)
        }
    }.flowOn(Dispatchers.IO)

    // sampling for live charts screen

    // memory info sampling
    val memoryUsageFlow = flow {
        while (true) {
            val info = ActivityManager.MemoryInfo()

            activityManager.getMemoryInfo(info)
            val usedMemory = info.totalMem - info.availMem

            filterFlowSamples(FlowType.MEMORY)
            memoryUsageSamples.add(
                FlowSample(
                    value = convertBytesToGigaBytes(usedMemory),
                    timestamp = Calendar.getInstance().timeInMillis
                )
            )

            emit(memoryUsageSamples.map {
                it.value
            }.toMutableList())

            delay(STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS,
                replayExpirationMillis = 0L
            ),
            initialValue = mutableListOf()
        )

    // cpu frequency sampling
    val cpuFrequencyFlow = flow {
        while (true) {
            val trackedCore = uiState.value.coreTracked
            val path = String.format(CORE_FREQUENCY_PATH, trackedCore)

            val frequencyKHz = File(path).readText().trim().toInt()

            filterFlowSamples(FlowType.FREQUENCY)
            cpuFrequencySamples.add(
                FlowSample(
                    value = convertKHzToGHz(frequencyKHz),
                    timestamp = Calendar.getInstance().timeInMillis
                )
            )

            emit(cpuFrequencySamples.map {
                it.value
            }.toMutableList())

            delay(STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS,
                replayExpirationMillis = 0L
            ),
            initialValue = mutableListOf()
        )

    // cpu load sampling
    val cpuLoadFlow = flow {
        while (true) {
            val process = Runtime.getRuntime().exec(UPTIME_COMMAND)
            val uptimeOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor()

            filterFlowSamples(FlowType.LOAD)
            cpuLoadSamples.add(
                FlowSample(
                    value = getLoadAverageFromUptimeCommandOutput(uptimeOutput, LoadAverageTypes.LAST_MINUTE),
                    timestamp = Calendar.getInstance().timeInMillis
                )
            )

            emit(cpuLoadSamples.map {
                it.value
            }.toMutableList())

            delay(STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS,
                replayExpirationMillis = 0L
            ),
            initialValue = mutableListOf()
        )

    fun changeTrackedCore(coreNumber: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = coreNumber
            )
        }

        cpuFrequencySamples.clear()
    }

    private fun filterFlowSamples(flowType: FlowType) {
        when (flowType) {
            FlowType.MEMORY -> {
                if (memoryUsageSamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - memoryUsageSamples[memoryUsageSamples.size - 1].timestamp > 5L * STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
                    memoryUsageSamples.clear()

                if (memoryUsageSamples.size >= NUMBER_OF_VALUES_TRACKED)
                    memoryUsageSamples.removeAt(0)
            }

            FlowType.FREQUENCY -> {
                if (cpuFrequencySamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - cpuFrequencySamples[cpuFrequencySamples.size - 1].timestamp > 5L * STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
                    cpuFrequencySamples.clear()

                if (cpuFrequencySamples.size >= NUMBER_OF_VALUES_TRACKED)
                    cpuFrequencySamples.removeAt(0)
            }

            FlowType.LOAD -> {
                if (cpuLoadSamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - cpuLoadSamples[cpuLoadSamples.size - 1].timestamp > 5L * STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
                    cpuLoadSamples.clear()

                if (cpuLoadSamples.size >= NUMBER_OF_VALUES_TRACKED)
                    cpuLoadSamples.removeAt(0)
            }
        }
    }

    fun getTotalMemory(): Float {
        return totalMemory
    }

    fun getNumCores(): Int {
        return numberOfCores
    }

    fun onPreferenceValueChanged(preferenceKey : String, newValue : String) {
        viewModelScope.launch {
            preferencesManager.updatePreferenceValue(preferenceKey, newValue)
        }
    }

}