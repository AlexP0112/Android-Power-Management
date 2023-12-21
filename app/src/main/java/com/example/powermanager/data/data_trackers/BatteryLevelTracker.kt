package com.example.powermanager.data.data_trackers

import com.example.powermanager.utils.BATTERY_LEVEL_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.NUMBER_OF_MILLIS_IN_A_DAY
import java.util.Calendar

data class BatteryLevelInfo(
    var level: Int,
    val timestamp: Long
)

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
        val step = (endTime - startTime).toDouble() / BATTERY_LEVEL_NUMBER_OF_SAMPLES.toDouble()
        val result: MutableList<BatteryLevelInfo> = mutableListOf()

        // add the timestamps
        for (i in 0 .. BATTERY_LEVEL_NUMBER_OF_SAMPLES) {
            result.add(BatteryLevelInfo(0, (startTime.toDouble() + i.toDouble() * step).toLong()) )
        }

        // determine the level for the timestamps that were added
        var levelIndex = 0
        for (index in 0 .. BATTERY_LEVEL_NUMBER_OF_SAMPLES) {
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