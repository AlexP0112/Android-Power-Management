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
import com.example.powermanager.control.cpufreq.CpuFreqManager
import com.example.powermanager.control.cpufreq.CpuFreqPolicy
import com.example.powermanager.control.cpufreq.CpuHotplugManager
import com.example.powermanager.control.storage.CpuConfiguration
import com.example.powermanager.control.storage.CpuConfigurationsStorageManager
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
import com.example.powermanager.recording.recorder.Recorder
import com.example.powermanager.recording.storage.RecordingResult
import com.example.powermanager.recording.storage.RecordingsStorageManager
import com.example.powermanager.ui.state.ControlScreenUiState
import com.example.powermanager.ui.state.HomeScreenUiState
import com.example.powermanager.ui.state.LiveChartsScreenUiState
import com.example.powermanager.ui.state.RecordingScreensUiState
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
import com.example.powermanager.utils.NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
import com.example.powermanager.utils.POLICY_CURRENT_FREQUENCY_PATH
import com.example.powermanager.utils.RECORDING_RESULTS_DIRECTORY_NAME
import com.example.powermanager.utils.SAVED_CPU_CONFIGURATIONS_DIRECTORY_NAME
import com.example.powermanager.utils.STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.convertBytesToGigaBytes
import com.example.powermanager.utils.convertKHzToGHz
import com.example.powermanager.utils.convertMicroAmpsToMilliAmps
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

    // screens ui states

    private val _homeScreenUiState : MutableStateFlow<HomeScreenUiState> = MutableStateFlow(
        HomeScreenUiState()
    )
    val homeScreenUiState : StateFlow<HomeScreenUiState> = _homeScreenUiState.asStateFlow()

    private val _liveChartsScreenUiState : MutableStateFlow<LiveChartsScreenUiState> = MutableStateFlow(
        LiveChartsScreenUiState()
    )
    val liveChartsScreenUiState : StateFlow<LiveChartsScreenUiState> = _liveChartsScreenUiState.asStateFlow()

    private val _controlScreenUiState : MutableStateFlow<ControlScreenUiState>
    val controlScreenUiState: StateFlow<ControlScreenUiState>

    private val _recordingScreensUiState : MutableStateFlow<RecordingScreensUiState>
    val recordingScreensUiState: StateFlow<RecordingScreensUiState>

    // constants determined at startup
    private val totalMemory: Float
    private val totalNumberOfCores : Int
    private val systemBootTimestamp : Long
    private val availableScalingGovernors : List<String>
    private val masterCores : List<Int>
    // map from policy name to its characteristics
    private val cpuFreqPolicies : Map<String, CpuFreqPolicy>
    // map from core index to the name of the policy it belongs to
    private val coreToPolicy : Map<Int, String>

    // directories where persistent data is stored
    private val cpuConfigurationsDirectory : File
    private val recordingResultsDirectory : File

    // managers
    private val activityManager : ActivityManager
    private val powerManager: PowerManager
    private val batteryManager: BatteryManager
    private val notificationManager: NotificationManager

    private val preferencesManager: PreferencesManager

    // flow samples
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
        cpuConfigurationsDirectory = File(application.applicationContext.filesDir, SAVED_CPU_CONFIGURATIONS_DIRECTORY_NAME)

        // initialize other local members/constants
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        totalMemory = convertBytesToGigaBytes(info.totalMem)
        systemBootTimestamp = determineSystemBootTimestamp()

        availableScalingGovernors = CpuFreqManager.getAvailableScalingGovernors()
        masterCores = CpuFreqManager.determineMasterCores()

        totalNumberOfCores = CpuHotplugManager.determineTotalNumberOfCPUCores()
        cpuFreqPolicies = CpuFreqManager.determineAllCpuFreqPolicies()
        coreToPolicy = CpuFreqManager.getCoreToPolicyMap(cpuFreqPolicies)

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
        val onlineCores = CpuHotplugManager.getOnlineCores()
        val disabledCores = (0 until totalNumberOfCores).filter { !onlineCores.contains(it) }
        val policyToFrequencyLimitMHz : MutableMap<String, Int> = mutableMapOf()
        cpuFreqPolicies.keys.forEach { policyToFrequencyLimitMHz[it] = getCurrentMaxFrequencyForPolicyMhz(it) }

        _controlScreenUiState = MutableStateFlow(
            ControlScreenUiState(
            currentScalingGovernor = CpuFreqManager.getCurrentScalingGovernor(),
            savedConfigurations = CpuConfigurationsStorageManager.getSavedCpuConfigurationsNames(cpuConfigurationsDirectory),
            disabledCores = disabledCores,
            policyToFrequencyLimitMHz = policyToFrequencyLimitMHz
            )
        )
        controlScreenUiState = _controlScreenUiState.asStateFlow()

        _recordingScreensUiState = MutableStateFlow(
            RecordingScreensUiState(
            recordingResults = RecordingsStorageManager.getMostRecentRecordingResultsNames(numberOfRecordingsListedLimit, recordingResultsDirectory)
            )
        )
        recordingScreensUiState = _recordingScreensUiState.asStateFlow()
    }

    // constants retrieval

    fun getTotalMemory(): Float {
        return totalMemory
    }

    fun getTotalNumberOfCores(): Int {
        return totalNumberOfCores
    }

    fun getAvailableScalingGovernors() : List<String> {
        return availableScalingGovernors
    }

    fun getMasterCores() : List<Int> {
        return masterCores
    }

    fun getCpuFreqPolicies(): List<CpuFreqPolicy> {
        return cpuFreqPolicies.values.toList().sortedBy { it.name }
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
            val onlineCores = CpuHotplugManager.getOnlineCores()
            val policyToCurrentFrequencyKhz : MutableMap<String, Int> = mutableMapOf()

            // determine current frequency for each policy
            cpuFreqPolicies.keys.forEach { policyName ->
                val path = String.format(POLICY_CURRENT_FREQUENCY_PATH, policyName)
                val frequencyKHz = File(path).readText().trim().toInt()

                policyToCurrentFrequencyKhz[policyName] = frequencyKHz
            }

            // determine current frequency for each core that is online
            val cpuFrequenciesGHz : List<Float> = onlineCores.map { core ->
                convertKHzToGHz(policyToCurrentFrequencyKhz[coreToPolicy[core]!!]!!)
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
                    numberOfThreads = numberOfThreads,
                    onlineCores = onlineCores
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
            val trackedCore = liveChartsScreenUiState.value.coreTracked
            val onlineCores = CpuHotplugManager.getOnlineCores()

            val path = String.format(CORE_FREQUENCY_PATH, trackedCore)

            // if tracked core is not online emit value close to 0
            val frequencyKHz = if (onlineCores.contains(trackedCore)) File(path).readText().trim().toInt() else 1

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

    // recording events

    fun startRecording(
        samplingPeriod : Long,
        numberOfSamples : Int,
        sessionName : String,
        includeThreadCountInfo : Boolean
    ) {
        _recordingScreensUiState.update { currentState ->
            currentState.copy(
                isRecording = true
            )
        }

        viewModelScope.launch {
            Recorder.record(
                samplingPeriod = samplingPeriod,
                numberOfSamples = numberOfSamples,
                sessionName = sessionName,
                batteryManager = batteryManager,
                powerManager = powerManager,
                activityManager = activityManager,
                includeThreadCountInfo = includeThreadCountInfo,
                outputDirectory = recordingResultsDirectory,
                onRecordingFinished = { onRecordingFinished(it) },
                getNumberOfThreads = { getNumberOfProcessesOrThreads(false) }
            )
        }
    }

    private fun onRecordingFinished(savedFileName : String?) {
        if (savedFileName == null)
            return

        // send the notification, if it is enabled
        val notificationEnabled = PreferenceValueAdaptor.preferenceStringValueToActualValue(
            preferenceID = RECORDING_FINISHED_NOTIFICATION_ENABLED_ID,
            preferenceValueAsString = getPreferenceValue(RECORDING_FINISHED_NOTIFICATION_ENABLED_ID)) as Boolean

        if (notificationEnabled)
            sendRecordingFinishedNotification(savedFileName)

        // update state
        _recordingScreensUiState.update { currentState ->
            currentState.copy(
                isRecording = false,
                recordingResults = getMostRecentRecordingResultsNames()
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

    // ================ state handling ======================= //

    // home screen

    fun changeHomeScreenInfoDialogState(newValue: Boolean) {
        _homeScreenUiState.update { currentState ->
            currentState.copy(
                isCPULoadInfoDialogOpen = newValue
            )
        }
    }

    // live charts screen

    fun changeFrequencyChartTrackedCore(coreNumber: Int) {
        if (coreNumber == liveChartsScreenUiState.value.coreTracked)
            return

        _liveChartsScreenUiState.update { currentState ->
            currentState.copy(
                coreTracked = coreNumber
            )
        }

        cpuFrequencySamples.clear()
    }

    // recording screen

    fun onRecordingInspectButtonPressed(recordingName : String) {
        _recordingScreensUiState.update { currentState ->
            currentState.copy(
                currentlySelectedRecordingResult = recordingName
            )
        }
    }

    fun onRecordingDeleteButtonPressed(recordingName: String) {
        _recordingScreensUiState.update { currentState ->
            currentState.copy(
                currentlySelectedRecordingResult = recordingName
            )
        }
    }

    fun onConfirmRecordingDeletionRequest() {
        val deleted = RecordingsStorageManager.deleteRecordingResult(
            name = recordingScreensUiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )

        if (deleted) {
            _recordingScreensUiState.update { currentState ->
                currentState.copy(
                    recordingResults = getMostRecentRecordingResultsNames()
                )
            }
        }
    }

    fun changeSelectedRecordingResult(recordingName: String) {
        _recordingScreensUiState.update { currentState ->
            currentState.copy(
                currentlySelectedRecordingResult = recordingName
            )
        }
    }

    fun shareRecordingResult(recordingName : String, context: Context) {
        val jsonFile = File(recordingResultsDirectory, "${recordingName}$DOT_JSON")
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

        return RecordingsStorageManager.getMostRecentRecordingResultsNames(
            limit = limit,
            directory = recordingResultsDirectory
        )
    }

    fun getRecordingResultRawFileContent() : String {
        return RecordingsStorageManager.getFileContent(
            fileName = recordingScreensUiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )
    }

    fun getCurrentlySelectedRecordingResult() : RecordingResult {
        return RecordingsStorageManager.getRecordingResultForFileName(
            fileName = recordingScreensUiState.value.currentlySelectedRecordingResult,
            directory = recordingResultsDirectory
        )!!
    }

    // control screen

    fun changeScalingGovernor(newGovernor: String) {
        _controlScreenUiState.update { currentState ->
            currentState.copy(
                currentScalingGovernor = newGovernor
            )
        }

        viewModelScope.launch {
            CpuFreqManager.changeScalingGovernor(
                newGovernor = newGovernor,
                policyNames = cpuFreqPolicies.keys.toList()
            )
        }
    }

    fun onCpuConfigurationInspectButtonPressed(configurationName : String) {
        _controlScreenUiState.update { currentState ->
            currentState.copy(
                currentlySelectedCpuConfiguration = configurationName
            )
        }
    }

    fun onCpuConfigurationDeleteButtonPressed(configurationName: String) {
        _controlScreenUiState.update { currentState ->
            currentState.copy(
                currentlySelectedCpuConfiguration = configurationName
            )
        }
    }

    fun onConfirmCpuConfigurationDeletionRequest() {
        CpuConfigurationsStorageManager.deleteConfiguration(
            name = controlScreenUiState.value.currentlySelectedCpuConfiguration,
            directory = cpuConfigurationsDirectory
        )

        _controlScreenUiState.update { currentState ->
            currentState.copy(
                savedConfigurations = CpuConfigurationsStorageManager.getSavedCpuConfigurationsNames(cpuConfigurationsDirectory)
            )
        }
    }

    fun applySelectedCpuConfiguration(configurationName: String) {
        val configuration = CpuConfigurationsStorageManager.getCpuConfigurationForFileName(
            fileName = configurationName,
            directory = cpuConfigurationsDirectory
        ) ?: return

        // update ui state
        val disabledCores = (0 until totalNumberOfCores).filter { it !in configuration.onlineCores }

        _controlScreenUiState.update { currentState ->
            currentState.copy(
                disabledCores = disabledCores,
                currentScalingGovernor = configuration.scalingGovernor,
                policyToFrequencyLimitMHz = configuration.policyToFrequencyLimitMHz
            )
        }

        viewModelScope.launch {
            CpuFreqManager.applyCpuConfiguration(
                configuration = configuration,
                policyNames = cpuFreqPolicies.keys.toList(),
                numberOfCores = totalNumberOfCores
            )
        }
    }

    fun getSelectedConfigurationFileContent() : String {
        return CpuConfigurationsStorageManager.getFileContent(
            fileName = controlScreenUiState.value.currentlySelectedCpuConfiguration,
            directory = cpuConfigurationsDirectory
        )
    }

    fun saveCurrentCpuConfiguration(configurationName: String) {
        val onlineCores = (0 until totalNumberOfCores).filter { it !in controlScreenUiState.value.disabledCores }

        val currentConfiguration = CpuConfiguration(
            name = configurationName,
            scalingGovernor = controlScreenUiState.value.currentScalingGovernor,
            onlineCores = onlineCores,
            policyToFrequencyLimitMHz = controlScreenUiState.value.policyToFrequencyLimitMHz
        )

        CpuConfigurationsStorageManager.saveCpuConfiguration(
            configuration = currentConfiguration,
            directory = cpuConfigurationsDirectory
        )

        _controlScreenUiState.update { currentState ->
            currentState.copy(
                savedConfigurations = CpuConfigurationsStorageManager.getSavedCpuConfigurationsNames(cpuConfigurationsDirectory)
            )
        }
    }

    private fun getCurrentMaxFrequencyForPolicyMhz(policyName: String) : Int {
        return CpuFreqManager.getCurrentMaxFrequencyForPolicyKhz(policyName) / NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
    }

    fun changeMaxFrequencyForPolicy(policyName : String, maxFrequencyMhz: Int) {
        val newLimits : MutableMap<String, Int> = mutableMapOf()
        controlScreenUiState.value.policyToFrequencyLimitMHz.forEach { (key, value) ->
            newLimits[key] = value
        }

        newLimits[policyName] = maxFrequencyMhz

        _controlScreenUiState.update { currentState ->
            currentState.copy(
                policyToFrequencyLimitMHz = newLimits
            )
        }

        CpuFreqManager.changeMaxFrequencyForPolicy(
            policyName = policyName,
            maxFrequencyKhz = maxFrequencyMhz * NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ
        )
    }

    fun changeCoreEnabledState(coreIndex : Int, enable: Boolean) {
        if (enable && controlScreenUiState.value.disabledCores.contains(coreIndex)) {
            // enable core
            _controlScreenUiState.update { currentState ->
                currentState.copy(
                    disabledCores = controlScreenUiState.value.disabledCores.filter { it != coreIndex }
                )
            }
        } else if (!enable && !controlScreenUiState.value.disabledCores.contains(coreIndex)) {
            // disable core
            val previouslyDisabledCores = controlScreenUiState.value.disabledCores.toMutableList()
            previouslyDisabledCores.add(coreIndex)

            _controlScreenUiState.update { currentState ->
                currentState.copy(
                    disabledCores = previouslyDisabledCores.toList()
                )
            }
        }

        CpuHotplugManager.changeCoreState(coreIndex, enable)
    }

    // preferences

    fun onPreferenceValueChanged(preferenceKey : String, newValue : String) {
        viewModelScope.launch {
            preferencesManager.updatePreferenceValue(preferenceKey, newValue)
        }

        if (preferenceKey == NUMBER_OF_RECORDINGS_LISTED_ID) {
            _recordingScreensUiState.update { currentState ->
                currentState.copy(
                    recordingResults = getMostRecentRecordingResultsNames()
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
