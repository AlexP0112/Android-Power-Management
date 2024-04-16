package com.example.powermanager.data.battery

import com.example.powermanager.utils.BATTERY_LEVEL_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.MILLIS_IN_AN_HOUR
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
    }

    fun getRecordsAtFixedTimeInterval(
        numberOfHoursTracked : Long
    ): MutableList<BatteryLevelInfo> {
        // filter the records according to the battery_chart_tracked_period preference
        records = records.dropWhile {
            record -> Calendar.getInstance().timeInMillis - record.timestamp > numberOfHoursTracked * MILLIS_IN_AN_HOUR
        }.toMutableList()

        val currentTimeLong = Calendar.getInstance().time.time
        val finalRecords = records.toMutableList()

        if (finalRecords.isEmpty())
            return mutableListOf()

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

    // only for testing purposes
    fun clearRecords() {
        records.clear()
    }

}
