package com.example.powermanager.data.sampling

import android.app.ActivityManager
import android.content.Context
import com.example.powermanager.data.data_trackers.CPUFrequencyTracker
import com.example.powermanager.data.data_trackers.CPULoadTracker
import com.example.powermanager.data.data_trackers.MemoryLoadTracker
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.utils.CORE_FREQUENCY_PATH
import com.example.powermanager.utils.STATISTICS_SCREEN_SAMPLING_RATE_MILLIS
import com.example.powermanager.utils.UPTIME_COMMAND
import com.example.powermanager.utils.convertKHzToGHz
import com.example.powermanager.utils.getGigaBytesFromBytes
import com.example.powermanager.utils.parseUptimeCommandOutput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object StatisticsScreenSamplingService {
    fun startSampling(applicationContext : Context, model: AppModel) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)

                // memory info sampling
                val info = ActivityManager.MemoryInfo()

                (am as ActivityManager).getMemoryInfo(info)
                val usedMemory = info.totalMem - info.availMem

                MemoryLoadTracker.addValue(getGigaBytesFromBytes(usedMemory))

                // cpu frequency sampling
                val trackedCore = model.uiState.value.coreTracked
                val path = String.format(CORE_FREQUENCY_PATH, trackedCore)

                val frequencyKHz = File(path).readText().trim().toInt()
                CPUFrequencyTracker.addValue(convertKHzToGHz(frequencyKHz))

                // cpu load sampling, parse the output of the "uptime" Linux command
                var uptimeOutput: String
                withContext(Dispatchers.IO) {
                    val process = Runtime.getRuntime().exec(UPTIME_COMMAND)
                    uptimeOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
                    process.waitFor()
                }
                CPULoadTracker.addValue(parseUptimeCommandOutput(uptimeOutput))

                withContext(Dispatchers.IO) {
                    Thread.sleep(STATISTICS_SCREEN_SAMPLING_RATE_MILLIS)
                }

                // check if sampling should finish (after 100 seconds of background sampling)
                if (model.shouldEndSampling()) {
                    model.endSampling()
                    break
                }
            }
        }
    }
}