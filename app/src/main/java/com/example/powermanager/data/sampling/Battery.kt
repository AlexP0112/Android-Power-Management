package com.example.powermanager.data.sampling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Calendar
import kotlin.math.roundToInt

data class BatteryLevelInfo(
    var level: Int,
    val timestamp: Long
)

const val NUMBER_OF_MILLIS_IN_A_DAY = 1000L * 60 * 60 * 24
const val DEFAULT_NUMBER_OF_SAMPLES = 30

class BatteryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val batteryLevel: Int? = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val batteryScale: Int? = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (batteryLevel == null || batteryLevel == -1 || batteryScale == null || batteryLevel == -1)
            return

        val percentage = ((batteryLevel * 100) / batteryScale.toFloat()).roundToInt()
        val timestamp = Calendar.getInstance().time.time

        BatteryLevelTracker.addRecord(BatteryLevelInfo(level = percentage, timestamp = timestamp))
    }
}

object BatteryLevelTracker {
    private var records: MutableList<BatteryLevelInfo> = mutableListOf()

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

        // only keep records from the last 24h
        records = records.dropWhile { record -> info.timestamp - record.timestamp > NUMBER_OF_MILLIS_IN_A_DAY }.toMutableList()
    }

    fun getRecordsAtFixedTimeInterval(): MutableList<BatteryLevelInfo> {
        val currentTimeLong = Calendar.getInstance().time.time
        val finalRecords = records.toMutableList()

        // we need to have at least 2 records
        if (finalRecords.size == 1)
            finalRecords.add(BatteryLevelInfo(level = records[0].level, timestamp = currentTimeLong))

        // split the period of time that was recorded into equal time intervals (linear interpolation)
        val startTime = finalRecords[0].timestamp
        val endTime = finalRecords[finalRecords.size - 1].timestamp
        val step = (endTime - startTime).toDouble() / DEFAULT_NUMBER_OF_SAMPLES.toDouble()
        val result: MutableList<BatteryLevelInfo> = mutableListOf()

        // add the timestamps
        for (i in 0 .. DEFAULT_NUMBER_OF_SAMPLES) {
            result.add(BatteryLevelInfo(0, (startTime.toDouble() + i.toDouble() * step).toLong()) )
        }

        // determine the level for the timestamps that were added
        var levelIndex = 0
        for (index in 0 .. DEFAULT_NUMBER_OF_SAMPLES) {
            val currentTimestamp = result[index].timestamp
            while (levelIndex < finalRecords.size && finalRecords[levelIndex].timestamp < currentTimestamp)
                levelIndex++

            result[index].level = finalRecords[if (levelIndex != 0) levelIndex - 1 else 0].level
        }

        // last record should contain the last battery level that was measured
        result[result.size - 1].level = finalRecords[finalRecords.size - 1].level

        return result
    }

}