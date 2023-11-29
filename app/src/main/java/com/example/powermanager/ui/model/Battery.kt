package com.example.powermanager.ui.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

data class BatteryLevelInfo(
    val level: Int,
    val timestamp: Date
)

class BatteryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val batteryLevel: Int? = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val batteryScale: Int? = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (batteryLevel == null || batteryLevel == -1 || batteryScale == null || batteryLevel == -1)
            return

        val percentage = ((batteryLevel * 100) / batteryScale.toFloat()).roundToInt()
        val timestamp = Calendar.getInstance().time

        BatteryLevelTracker.addRecord(BatteryLevelInfo(percentage, timestamp))
    }
}

object BatteryLevelTracker {
    private val records: MutableList<BatteryLevelInfo> = mutableListOf()

    fun addRecord(info: BatteryLevelInfo) {
        if (records.isEmpty()) {
            records.add(info)
            return
        }

        // only add records when there is a change in battery level
        val lastRecord = records[records.size - 1]
        if (lastRecord.level == info.level)
            return

        records.add(info)
    }

    fun getRecordsAtSamplingRate(samplingRateMinutes: Int): MutableList<BatteryLevelInfo> {
        val result:MutableList<BatteryLevelInfo> = mutableListOf()

        if (records.isEmpty())
            return result

        result.add(records[0])

        var currentIndex = 1

        while (currentIndex < records.size) {
            currentIndex++
        }

        return result
    }

}