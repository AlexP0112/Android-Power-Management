package com.example.powermanager.ui.model

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.powermanager.utils.getGigaBytesFromBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val NUMBER_OF_VALUES_TRACKED = 60
const val SAMPLING_RATE_MILLIS = 1000L

object MemoryService {
    fun startSampling(applicationContext : Context, model: AppModel) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
                val info = ActivityManager.MemoryInfo()

                (am as ActivityManager).getMemoryInfo(info)
                val usedMemory = info.totalMem - info.availMem

                MemoryLoadTracker.addValue(getGigaBytesFromBytes(usedMemory))

                withContext(Dispatchers.IO) {
                    Thread.sleep(SAMPLING_RATE_MILLIS)
                }

                // check if sampling should finish (after 3 minutes of background sampling)
                if (model.shouldEndSampling()) {
                    model.endSampling()
                    break
                }
            }
        }
    }
}

object MemoryLoadTracker {
    private var values: SnapshotStateList<Float> = mutableStateListOf()

    fun addValue(value: Float) {
        values.add(value)

        // only keep records from the last minute
        if (values.size > NUMBER_OF_VALUES_TRACKED)
            values.removeAt(0)
    }

    fun getValues() : SnapshotStateList<Float> {
        return values
    }

    fun clearValues() {
        values.clear()
    }
}