package com.example.powermanager.data.sampling

import android.app.ActivityManager
import android.content.Context
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.utils.convertKHzToGHz
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val SAMPLING_RATE_MILLIS = 1000L
const val FREQUENCY_READ_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq"

object SamplingService {
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
                val path = String.format(FREQUENCY_READ_PATH, trackedCore)

                val frequencyKHz = File(path).readText().trim().toInt()
                CPUFrequencyTracker.addValue(convertKHzToGHz(frequencyKHz))

                withContext(Dispatchers.IO) {
                    Thread.sleep(SAMPLING_RATE_MILLIS)
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