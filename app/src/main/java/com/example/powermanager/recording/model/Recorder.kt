package com.example.powermanager.recording.model

import android.app.ActivityManager
import android.os.BatteryManager
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.recording.storage.RecordingStorageManager
import com.example.powermanager.utils.MILLIS_IN_A_SECOND
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.computeListAverage
import com.example.powermanager.utils.convertBytesToGigaBytes
import com.example.powermanager.utils.convertMicroAmpsToMilliAmps
import com.example.powermanager.utils.getDateTimeNiceString
import com.example.powermanager.utils.getListMaximum
import com.example.powermanager.utils.getLoadAverageFromUptimeCommandOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.roundToInt

object Recorder {

    suspend fun record(
        samplingPeriod: Long,
        numberOfSamples: Int,
        sessionName: String,
        batteryManager: BatteryManager,
        activityManager: ActivityManager,
        outputDirectory : File,
        onRecordingFinished: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val batteryChargeValues : MutableList<Int> = mutableListOf()
            val memoryUsedValues : MutableList<Float> = mutableListOf()
            val cpuLoadValues : MutableList<Float> = mutableListOf()

            // sampling
            for (i in 0 until numberOfSamples) {
                // battery sampling
                val batteryChargeCountMilliAmps = convertMicroAmpsToMilliAmps(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER))
                batteryChargeValues.add(batteryChargeCountMilliAmps)

                // memory sampling
                val info = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(info)
                val usedMemory = info.totalMem - info.availMem
                val usedMemoryGB = convertBytesToGigaBytes(usedMemory)
                memoryUsedValues.add(usedMemoryGB)

                // cpu load sampling
                val uptimeProcess = Runtime.getRuntime().exec(UPTIME_COMMAND)
                val uptimeOutput = BufferedReader(InputStreamReader(uptimeProcess.inputStream)).readText()
                uptimeProcess.waitFor()
                val load = getLoadAverageFromUptimeCommandOutput(uptimeOutput, LoadAverageTypes.LAST_MINUTE)
                cpuLoadValues.add(load)

                delay(samplingPeriod)
            }

            // compute final statistics and aggregate the results
            val peakMemoryUsage = getListMaximum(memoryUsedValues)
            val peakCpuLoad = getListMaximum(cpuLoadValues)

            val averageMemoryUsage = computeListAverage(memoryUsedValues)
            val averageCpuLoad = computeListAverage(cpuLoadValues)

            val sessionNumberOfSeconds = (samplingPeriod.toFloat() / MILLIS_IN_A_SECOND.toFloat() * numberOfSamples).roundToInt()

            val result = RecordingResult(
                sessionName = sessionName,
                timestamp = getDateTimeNiceString(),
                duration = "$sessionNumberOfSeconds seconds",
                numberOfSamples = numberOfSamples,
                batteryChargeValues = batteryChargeValues,
                memoryUsedValues = memoryUsedValues,
                cpuLoadValues = cpuLoadValues,
                peakMemoryUsed = peakMemoryUsage,
                averageMemoryUsed = averageMemoryUsage,
                peakCpuLoad = peakCpuLoad,
                averageCpuLoad = averageCpuLoad
            )

            // save the result in a JSON file
            val savedFileName = RecordingStorageManager.saveRecordingResult(
                result = result,
                directory = outputDirectory
            )

            onRecordingFinished(savedFileName)
        }
    }

}