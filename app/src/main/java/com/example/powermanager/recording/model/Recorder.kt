package com.example.powermanager.recording.model

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.recording.storage.RecordingStorageManager
import com.example.powermanager.utils.READ_NETWORK_INTERFACES_STATS_COMMAND
import com.example.powermanager.utils.RECORDING_TAG
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.WAKELOCK_TAG
import com.example.powermanager.utils.WAKELOCK_TIMEOUT
import com.example.powermanager.utils.computeListAverage
import com.example.powermanager.utils.convertBytesToGigaBytes
import com.example.powermanager.utils.convertMicroAmpsToMilliAmps
import com.example.powermanager.utils.getBytesSentAndReceivedByAllInterfacesFromFileContent
import com.example.powermanager.utils.getDateTimeNiceString
import com.example.powermanager.utils.getListMaximum
import com.example.powermanager.utils.getLoadAverageFromUptimeCommandOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object Recorder {

    @SuppressLint("InvalidWakeLockTag")
    suspend fun record(
        samplingPeriod: Long,
        numberOfSamples: Int,
        sessionName: String,
        batteryManager: BatteryManager,
        powerManager: PowerManager,
        activityManager: ActivityManager,
        outputDirectory: File,
        includeThreadCountInfo : Boolean,
        onRecordingFinished: (String) -> Unit,
        getNumberOfThreads: () -> Int
    ) {
        withContext(Dispatchers.IO) {
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
                for (i in 0 until numberOfSamples) {
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

                    if (i != numberOfSamples - 1)
                        delay(samplingPeriod)
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
                    numberOfSamples = numberOfSamples,
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
                val savedFileName = RecordingStorageManager.saveRecordingResult(
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

    /*
     * Returns a list of 2 long numbers representing the total number of bytes
     * received / sent on any internet interface since system boot
     */
    private fun getBytesSentAndReceivedByAllInterfaces(): List<Long> {
        val process = Runtime.getRuntime().exec(READ_NETWORK_INTERFACES_STATS_COMMAND)
        val processOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return getBytesSentAndReceivedByAllInterfacesFromFileContent(processOutput)
    }

}
