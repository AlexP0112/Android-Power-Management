package com.example.powermanager.ui.model

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.powermanager.R
import com.example.powermanager.preferences.HOME_SCREEN_SAMPLING_PERIOD_ID
import com.example.powermanager.preferences.LIVE_CHARTS_SAMPLING_PERIOD_ID
import com.example.powermanager.preferences.LIVE_CHARTS_TRACKED_PERIOD_ID
import com.example.powermanager.preferences.LOAD_AVERAGE_TYPE_ID
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.preferences.NUMBER_OF_RECORDINGS_LISTED_ID
import com.example.powermanager.preferences.PreferenceProperties
import com.example.powermanager.preferences.PreferenceValueAdaptor
import com.example.powermanager.preferences.PreferencesManager
import com.example.powermanager.preferences.RECORDING_FINISHED_NOTIFICATION_ENABLED_ID
import com.example.powermanager.recording.model.Recorder
import com.example.powermanager.recording.model.RecordingResult
import com.example.powermanager.recording.storage.RecordingStorageManager
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.CORE_FREQUENCY_PATH
import com.example.powermanager.utils.DOT_JSON
import com.example.powermanager.utils.DOT_PROVIDER
import com.example.powermanager.utils.FAILED_TO_DETERMINE
import com.example.powermanager.utils.GET_NUMBER_OF_PROCESSES_COMMAND
import com.example.powermanager.utils.GET_NUMBER_OF_THREADS_COMMAND
import com.example.powermanager.utils.JSON_MIME_TYPE
import com.example.powermanager.utils.MILLIS_IN_A_SECOND
import com.example.powermanager.utils.NOTIFICATION_CHANNEL_ID
import com.example.powermanager.utils.NOTIFICATION_ID
import com.example.powermanager.utils.NOTIFICATION_TEXT
import com.example.powermanager.utils.NOTIFICATION_TITLE
import com.example.powermanager.utils.RECORDING_RESULTS_DIRECTORY_NAME
import com.example.powermanager.utils.STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS
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

    private val _uiState : MutableStateFlow<AppUiState>
    val uiState: StateFlow<AppUiState>

    private val totalMemory: Float
    private val numberOfCores : Int
    private val systemBootTimestamp : Long

    private val recordingResultsDirectory : File

    private val activityManager : ActivityManager
    private val powerManager: PowerManager
    private val batteryManager: BatteryManager
    private val notificationManager: NotificationManager

    private val preferencesManager: PreferencesManager

    private var memoryUsageSamples: MutableList<FlowSample> = mutableListOf()
    private var cpuFrequencySamples: MutableList<FlowSample> = mutableListOf()
    private var cpuLoadSamples: MutableList<FlowSample> = mutableListOf()

    init {
        // initialize managers
        activityManager = application.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        powerManager = application.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        batteryManager = application.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        notificationManager = application.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferencesManager = PreferencesManager(application.applicationContext)

        recordingResultsDirectory = File(application.applicationContext.filesDir, RECORDING_RESULTS_DIRECTORY_NAME)

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

            val numberOfRecordingsListedLimit = PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = NUMBER_OF_RECORDINGS_LISTED_ID,
                preferenceValueAsString = getPreferenceValue(NUMBER_OF_RECORDINGS_LISTED_ID)) as Int
            onPreferenceValueChanged(NUMBER_OF_RECORDINGS_LISTED_ID, numberOfRecordingsListedLimit.toString())
        }

        // initialize state
        val numberOfRecordingsListedLimit = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = NUMBER_OF_RECORDINGS_LISTED_ID,
            preferenceValueAsString = getPreferenceValue(NUMBER_OF_RECORDINGS_LISTED_ID)) as Int

        _uiState = MutableStateFlow(AppUiState(
            recordingResults = RecordingStorageManager.getMostRecentRecordingResultsNames(numberOfRecordingsListedLimit, recordingResultsDirectory)
        ))
        uiState = _uiState.asStateFlow()
    }

    // constants determined at startup

    fun getTotalMemory(): Float {
        return totalMemory
    }

    fun getNumCores(): Int {
        return numberOfCores
    }

    // sampling for home screen

    @SuppressLint("NewApi")
    val homeScreenInfoFlow = flow {
        while (true) {
            val loadAverageType = PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = LOAD_AVERAGE_TYPE_ID,
                preferenceValueAsString = getPreferenceValue(LOAD_AVERAGE_TYPE_ID)) as LoadAverageTypes

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

            val loadAverage = getLoadAverage(loadAverageType)
            val numberOfProcesses = getNumberOfProcessesOrThreads(true)
            val numberOfThreads = getNumberOfProcessesOrThreads(false)

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
                    cpuLoad = loadAverage,
                    systemUptimeString = uptimeString,
                    numberOfProcesses = numberOfProcesses,
                    numberOfThreads = numberOfThreads
                )
            )

            delay(PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = HOME_SCREEN_SAMPLING_PERIOD_ID,
                preferenceValueAsString = getPreferenceValue(HOME_SCREEN_SAMPLING_PERIOD_ID)) as Long
            )
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

            delay(PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = LIVE_CHARTS_SAMPLING_PERIOD_ID,
                preferenceValueAsString = getPreferenceValue(LIVE_CHARTS_SAMPLING_PERIOD_ID)) as Long
            )
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

            delay(PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = LIVE_CHARTS_SAMPLING_PERIOD_ID,
                preferenceValueAsString = getPreferenceValue(LIVE_CHARTS_SAMPLING_PERIOD_ID)) as Long
            )
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
            val loadAverageType = PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = LOAD_AVERAGE_TYPE_ID,
                preferenceValueAsString = getPreferenceValue(LOAD_AVERAGE_TYPE_ID)) as LoadAverageTypes

            val loadAverage = getLoadAverage(loadAverageType)

            filterFlowSamples(FlowType.LOAD)
            cpuLoadSamples.add(
                FlowSample(
                    value = loadAverage,
                    timestamp = Calendar.getInstance().timeInMillis
                )
            )

            emit(cpuLoadSamples.map {
                it.value
            }.toMutableList())

            delay(PreferenceValueAdaptor.preferenceStringValueToActualValue(
                preferenceID = LIVE_CHARTS_SAMPLING_PERIOD_ID,
                preferenceValueAsString = getPreferenceValue(LIVE_CHARTS_SAMPLING_PERIOD_ID)) as Long
            )
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
                coreTracked = coreNumber,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }

        cpuFrequencySamples.clear()
    }

    private fun filterFlowSamples(flowType: FlowType) {
        val samplingPeriod = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = LIVE_CHARTS_SAMPLING_PERIOD_ID,
            preferenceValueAsString = getPreferenceValue(LIVE_CHARTS_SAMPLING_PERIOD_ID)) as Long

        val trackedPeriodSeconds = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = LIVE_CHARTS_TRACKED_PERIOD_ID,
            preferenceValueAsString = getPreferenceValue(LIVE_CHARTS_TRACKED_PERIOD_ID)) as Long

        val numberOfValuesTracked = trackedPeriodSeconds * MILLIS_IN_A_SECOND / samplingPeriod

        when (flowType) {
            FlowType.MEMORY -> {
                if (memoryUsageSamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - memoryUsageSamples[memoryUsageSamples.size - 1].timestamp > 4L * samplingPeriod)
                    memoryUsageSamples.clear()

                if (memoryUsageSamples.size >= numberOfValuesTracked)
                    memoryUsageSamples = memoryUsageSamples.drop((memoryUsageSamples.size - numberOfValuesTracked + 1).toInt()).toMutableList()
            }

            FlowType.FREQUENCY -> {
                if (cpuFrequencySamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - cpuFrequencySamples[cpuFrequencySamples.size - 1].timestamp > 4L * samplingPeriod)
                    cpuFrequencySamples.clear()

                if (cpuFrequencySamples.size >= numberOfValuesTracked)
                    cpuFrequencySamples = cpuFrequencySamples.drop((cpuFrequencySamples.size - numberOfValuesTracked + 1).toInt()).toMutableList()
            }

            FlowType.LOAD -> {
                if (cpuLoadSamples.isNotEmpty() &&
                    Calendar.getInstance().timeInMillis - cpuLoadSamples[cpuLoadSamples.size - 1].timestamp > 4L * samplingPeriod)
                    cpuLoadSamples.clear()

                if (cpuLoadSamples.size >= numberOfValuesTracked)
                    cpuLoadSamples = cpuLoadSamples.drop((cpuLoadSamples.size - numberOfValuesTracked + 1).toInt()).toMutableList()
            }
        }
    }

    // recording

    fun startRecording() {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = true,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }

        viewModelScope.launch {
            Recorder.record(
                samplingPeriod = uiState.value.recordingSamplingPeriod,
                numberOfSamples = uiState.value.recordingNumberOfSamplesString.toInt(),
                sessionName = uiState.value.recordingSessionName,
                batteryManager = batteryManager,
                activityManager = activityManager,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo,
                outputDirectory = recordingResultsDirectory,
                onRecordingFinished = { onRecordingFinished(it) }
            ) { getNumberOfProcessesOrThreads(false) }
        }
    }

    private fun onRecordingFinished(savedFileName : String) {
        // send the notification, if it is enabled
        val notificationEnabled = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = RECORDING_FINISHED_NOTIFICATION_ENABLED_ID,
            preferenceValueAsString = getPreferenceValue(RECORDING_FINISHED_NOTIFICATION_ENABLED_ID)) as Boolean

        if (notificationEnabled)
            sendRecordingFinishedNotification(savedFileName)

        // update state
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = false,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = getMostRecentRecordingResultsNames(),
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }
    }

    private fun sendRecordingFinishedNotification(savedFileName: String) {
        val context = getApplication<Application>().applicationContext

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(String.format(NOTIFICATION_TEXT, savedFileName))
            .setSmallIcon(R.drawable.app_icon)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
    }

    fun changeRecordingSamplingPeriod(newValue: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = newValue,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }
    }

    fun changeRecordingNumberOfSamplesString(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = newValue,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }
    }

    fun changeRecordingSessionName(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = newValue,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo
            )
        }
    }

    fun changeIncludeThreadCountInfoOption(newValue : Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                includeThreadCountInfo = newValue
            )
        }
    }

    fun changeSelectedRecordingResult(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                coreTracked = uiState.value.coreTracked,
                isRecording = uiState.value.isRecording,
                recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                recordingSessionName = uiState.value.recordingSessionName,
                recordingResults = uiState.value.recordingResults,
                includeThreadCountInfo = uiState.value.includeThreadCountInfo,
                currentlySelectedRecordingResult = newValue
            )
        }
    }

    fun deleteRecordingResult() {
        val deleted = RecordingStorageManager.deleteRecordingResult(
            name = uiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )

        // update state, if the file was successfully deleted
        if (deleted) {
            _uiState.update { currentState ->
                currentState.copy(
                    coreTracked = uiState.value.coreTracked,
                    isRecording = uiState.value.isRecording,
                    recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                    recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                    recordingSessionName = uiState.value.recordingSessionName,
                    recordingResults = getMostRecentRecordingResultsNames(),
                    currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                    includeThreadCountInfo = uiState.value.includeThreadCountInfo
                )
            }
        }
    }

    fun shareRecordingResult(context: Context) {
        val jsonFile = File(recordingResultsDirectory, "${uiState.value.currentlySelectedRecordingResult}$DOT_JSON")
        val fileUri = FileProvider.getUriForFile(context, context.packageName + DOT_PROVIDER, jsonFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = JSON_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareAppChooser = Intent.createChooser(shareIntent, null)
        context.startActivity(shareAppChooser)
    }

    private fun getMostRecentRecordingResultsNames(): List<String> {
        val limit = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = NUMBER_OF_RECORDINGS_LISTED_ID,
            preferenceValueAsString = getPreferenceValue(NUMBER_OF_RECORDINGS_LISTED_ID)) as Int

        return RecordingStorageManager.getMostRecentRecordingResultsNames(
            limit = limit,
            directory = recordingResultsDirectory
        )
    }

    fun getRecordingResultRawFileContent() : String {
        return RecordingStorageManager.getFileContent(
            fileName = uiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )
    }

    fun getCurrentlySelectedRecordingResult() : RecordingResult {
        return RecordingStorageManager.getRecordingResultForFileName(
            fileName = uiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )!!
    }

    // preferences

    fun onPreferenceValueChanged(preferenceKey : String, newValue : String) {
        viewModelScope.launch {
            preferencesManager.updatePreferenceValue(preferenceKey, newValue)
        }

        if (preferenceKey == NUMBER_OF_RECORDINGS_LISTED_ID) {
            _uiState.update { currentState ->
                currentState.copy(
                    coreTracked = uiState.value.coreTracked,
                    isRecording = uiState.value.isRecording,
                    recordingSamplingPeriod = uiState.value.recordingSamplingPeriod,
                    recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
                    recordingSessionName = uiState.value.recordingSessionName,
                    recordingResults = getMostRecentRecordingResultsNames(),
                    currentlySelectedRecordingResult = uiState.value.currentlySelectedRecordingResult,
                    includeThreadCountInfo = uiState.value.includeThreadCountInfo
                )
            }
        }
    }

    fun getPreferenceValue(preferenceKey: String) : String {
        return preferencesManager.getCurrentValueForPreference(preferenceKey)
    }

    fun getPreferenceProperties(preferenceKey: String) : PreferenceProperties {
        return preferencesManager.getPreferenceProperties(preferenceKey)
    }

    // Linux commands invocation

    private fun getNumberOfProcessesOrThreads(processes: Boolean) : Int {
        val command = if (processes) GET_NUMBER_OF_PROCESSES_COMMAND else GET_NUMBER_OF_THREADS_COMMAND

        val process = Runtime.getRuntime().exec(command)
        val processOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return try {
            // subtract 1 to eliminate the header
            processOutput.trim().toInt() - 1
        } catch (_ : Exception) {
            0
        }
    }

    private fun getLoadAverage(loadAverageType: LoadAverageTypes) : Float {
        val process = Runtime.getRuntime().exec(UPTIME_COMMAND)
        val processOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return getLoadAverageFromUptimeCommandOutput(processOutput, loadAverageType)
    }

}
