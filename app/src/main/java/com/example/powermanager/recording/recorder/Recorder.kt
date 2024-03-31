package com.example.powermanager.recording.recorder

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.recording.storage.RecordingResult
import com.example.powermanager.recording.storage.RecordingsStorageManager
import com.example.powermanager.utils.ConversionUtils.convertBytesToGigaBytes
import com.example.powermanager.utils.ConversionUtils.convertMicroAmpsToMilliAmps
import com.example.powermanager.utils.FormattingUtils.getDateTimeNiceString
import com.example.powermanager.utils.LinuxCommandsUtils.getBytesSentAndReceivedByAllInternetInterfacesFromFileContent
import com.example.powermanager.utils.LinuxCommandsUtils.getLoadAverageFromUptimeCommandOutput
import com.example.powermanager.utils.LinuxCommandsUtils.readProtectedFileContent
import com.example.powermanager.utils.ListUtils.computeListAverage
import com.example.powermanager.utils.ListUtils.getListMaximum
import com.example.powermanager.utils.NETWORK_INTERFACES_STATS_PATH
import com.example.powermanager.utils.RECORDING_TAG
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.WAKELOCK_TAG
import com.example.powermanager.utils.WAKELOCK_TIMEOUT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object Recorder {

    // flag that signals that the user manually requested for the recording to stop
    private var stopRecording : Boolean = false

    @SuppressLint("InvalidWakeLockTag")
    suspend fun startRecording(
        samplingPeriod: Long,
        maximumNumberOfSamples: Int,
        sessionName: String,
        batteryManager: BatteryManager,
        powerManager: PowerManager,
        activityManager: ActivityManager,
        outputDirectory: File,
        includeThreadCountInfo : Boolean,
        onRecordingFinished: (String?) -> Unit,
        getNumberOfThreads: () -> Int
    ) {
        withContext(Dispatchers.IO) {
            stopRecording = false

            val batteryChargeValues : MutableList<Int> = mutableListOf()
            val memoryUsedValues : MutableList<Float> = mutableListOf()
            val cpuLoadValues : MutableList<Float> = mutableListOf()
            val numberOfThreadsValues : MutableList<Int> = mutableListOf()

            // acquire wake lock to keep the CPU on while sampling
            val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
            wakeLock.acquire(WAKELOCK_TIMEOUT)

            try {
                // get initial information about internet usage
                val initialInterfacesStats = getBytesSentAndReceivedByAllInterfaces()

                // sampling
                var sampleNumber = 0

                while(sampleNumber < maximumNumberOfSamples) {
                    // battery sampling
                    val batteryChargeCountMilliAmps =
                        convertMicroAmpsToMilliAmps(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                    batteryChargeValues.add(batteryChargeCountMilliAmps)

                    // memory sampling
                    val info = ActivityManager.MemoryInfo()
                    activityManager.getMemoryInfo(info)
                    val usedMemory = info.totalMem - info.availMem
                    val usedMemoryGB = convertBytesToGigaBytes(usedMemory)
                    memoryUsedValues.add(usedMemoryGB)

                    // cpu load sampling
                    val uptimeProcess = Runtime.getRuntime().exec(UPTIME_COMMAND)
                    val uptimeOutput =
                        BufferedReader(InputStreamReader(uptimeProcess.inputStream)).readText()
                    uptimeProcess.waitFor()
                    val load = getLoadAverageFromUptimeCommandOutput(
                        uptimeOutput,
                        LoadAverageTypes.LAST_MINUTE
                    )
                    cpuLoadValues.add(load)

                    // number of threads sampling
                    if (includeThreadCountInfo) {
                        val numberOfThreads = getNumberOfThreads()
                        numberOfThreadsValues.add(numberOfThreads)
                    }

                    if (sampleNumber != maximumNumberOfSamples - 1)
                        delay(samplingPeriod)

                    sampleNumber++
                    if (stopRecording)
                        break
                }

                val finalInterfacesStats = getBytesSentAndReceivedByAllInterfaces()

                // compute final statistics and aggregate the results
                val peakMemoryUsage = getListMaximum(memoryUsedValues)
                val peakCpuLoad = getListMaximum(cpuLoadValues)

                val averageMemoryUsage = computeListAverage(memoryUsedValues)
                val averageCpuLoad = computeListAverage(cpuLoadValues)

                val result = RecordingResult(
                    sessionName = sessionName,
                    timestamp = getDateTimeNiceString(),
                    samplingPeriodMillis = samplingPeriod,
                    numberOfSamples = sampleNumber,
                    batteryChargeValues = batteryChargeValues,
                    memoryUsedValues = memoryUsedValues,
                    cpuLoadValues = cpuLoadValues,
                    peakMemoryUsed = peakMemoryUsage,
                    numberOfBytesReceived = finalInterfacesStats[0] - initialInterfacesStats[0],
                    numberOfBytesSent = finalInterfacesStats[1] - initialInterfacesStats[1],
                    averageMemoryUsed = averageMemoryUsage,
                    peakCpuLoad = peakCpuLoad,
                    averageCpuLoad = averageCpuLoad,
                    numberOfThreadsValues = numberOfThreadsValues
                )

                // save the result in a JSON file
                val savedFileName = RecordingsStorageManager.saveRecordingResult(
                    result = result,
                    directory = outputDirectory
                )

                onRecordingFinished(savedFileName)

            } catch (e: Exception) {
                Log.e(RECORDING_TAG, e.stackTraceToString())
            } finally {
                // release the wake lock
                wakeLock.release()
            }
        }
    }

    fun stopRecording() {
        stopRecording = true
    }

    /*
     * Returns a list of 2 long numbers representing the total number of bytes
     * received / sent on any internet interface since system boot
     *
     * It reads from /proc/net/dev
     */
    private fun getBytesSentAndReceivedByAllInterfaces(): List<Long> {
        val fileContent = readProtectedFileContent(NETWORK_INTERFACES_STATS_PATH)

        return getBytesSentAndReceivedByAllInternetInterfacesFromFileContent(fileContent)
    }

}
