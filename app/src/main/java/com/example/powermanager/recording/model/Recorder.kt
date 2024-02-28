package com.example.powermanager.recording.model

import android.app.ActivityManager
import android.app.usage.NetworkStatsManager
import android.net.ConnectivityManager
import android.os.BatteryManager
import com.example.powermanager.preferences.LoadAverageTypes
import com.example.powermanager.recording.storage.RecordingStorageManager
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
import java.util.Calendar

object Recorder {

    suspend fun record(
        samplingPeriod: Long,
        numberOfSamples: Int,
        sessionName: String,
        batteryManager: BatteryManager,
        activityManager: ActivityManager,
        networkStatsManager: NetworkStatsManager,
        outputDirectory : File,
        onRecordingFinished: (String) -> Unit,
        getNumberOfThreads: () -> Int
    ) {
        withContext(Dispatchers.IO) {
            val batteryChargeValues : MutableList<Int> = mutableListOf()
            val memoryUsedValues : MutableList<Float> = mutableListOf()
            val cpuLoadValues : MutableList<Float> = mutableListOf()
            val numberOfThreadsValues : MutableList<Int> = mutableListOf()

            val startTimestamp : Long = Calendar.getInstance().timeInMillis

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

                // number of threads sampling
                val numberOfThreads = getNumberOfThreads()
                numberOfThreadsValues.add(numberOfThreads)

                if (i != numberOfSamples - 1)
                    delay(samplingPeriod)
            }

            val endTimestamp: Long = Calendar.getInstance().timeInMillis

            // compute final statistics and aggregate the results
            val peakMemoryUsage = getListMaximum(memoryUsedValues)
            val peakCpuLoad = getListMaximum(cpuLoadValues)

            val averageMemoryUsage = computeListAverage(memoryUsedValues)
            val averageCpuLoad = computeListAverage(cpuLoadValues)

            // network stats
            val wifiStats = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, null, startTimestamp, endTimestamp)
            val mobileDataStats = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, null, startTimestamp, endTimestamp)

            val totalBytesReceived = wifiStats.rxBytes + mobileDataStats.rxBytes
            val totalBytesSent = wifiStats.txBytes + mobileDataStats.txBytes

            val totalPacketsReceived = wifiStats.rxPackets + mobileDataStats.rxPackets
            val totalPacketsSent = wifiStats.txPackets + mobileDataStats.txPackets

            val result = RecordingResult(
                sessionName = sessionName,
                timestamp = getDateTimeNiceString(),
                samplingPeriodMillis = samplingPeriod,
                numberOfSamples = numberOfSamples,
                batteryChargeValues = batteryChargeValues,
                memoryUsedValues = memoryUsedValues,
                cpuLoadValues = cpuLoadValues,
                peakMemoryUsed = peakMemoryUsage,
                averageMemoryUsed = averageMemoryUsage,
                peakCpuLoad = peakCpuLoad,
                averageCpuLoad = averageCpuLoad,
                numberOfThreadsValues = numberOfThreadsValues,
                bytesRx = totalBytesReceived,
                bytesTx = totalBytesSent,
                packetsRx = totalPacketsReceived,
                packetsTx = totalPacketsSent
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
