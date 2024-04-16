package com.example.powermanager.data.battery

import com.example.powermanager.utils.BATTERY_LEVEL_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.MILLIS_IN_AN_HOUR
import com.example.powermanager.utils.MILLIS_IN_A_DAY
import com.example.powermanager.utils.MILLIS_IN_A_MINUTE
import org.junit.After
import org.junit.Test
import java.util.Calendar

class BatteryLevelTrackerTest {

    private val defaultNumberOfHoursTracked = 24L
    private val customNumberOfHoursTracked = 6L

    @After
    fun afterEach() {
        // clear the records in the tracker after each test
        BatteryLevelTracker.clearRecords()
    }

    @Test
    fun `get battery level records returns non-empty list if a record is added`() {
        BatteryLevelTracker.addRecord(BatteryLevelInfo(90, Calendar.getInstance().timeInMillis))
        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(defaultNumberOfHoursTracked)

        assert(result.isNotEmpty())
    }

    @Test
    fun `get battery level records returns empty list if no records are added`() {
        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(defaultNumberOfHoursTracked)

        assert(result.isEmpty())
    }

    @Test
    fun `correct number of records returned with less than 50 insertions`() {
        val numInserted = 25
        val startTimeMillis = Calendar.getInstance().timeInMillis - 1000L

        for (i in 0..numInserted)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(90 - i, startTimeMillis + 20L * i))

        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(defaultNumberOfHoursTracked)

        assert(result.size == BATTERY_LEVEL_NUMBER_OF_SAMPLES + 1)
    }

    @Test
    fun `correct number of records returned with more than 50 insertions`() {
        val numInserted = 75
        val startTimeMillis = Calendar.getInstance().timeInMillis - 2000L

        for (i in 0..numInserted)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(90 - i, startTimeMillis + 20L * i))

        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(defaultNumberOfHoursTracked)

        assert(result.size == BATTERY_LEVEL_NUMBER_OF_SAMPLES + 1)
    }

    @Test
    fun `returned records are filtered - last 24 hours`() {
        var startTimeMillis = Calendar.getInstance().timeInMillis - 2L * MILLIS_IN_A_DAY

        for (i in 0..4)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(90 - i, startTimeMillis + 20L * i))

        startTimeMillis += MILLIS_IN_A_DAY + MILLIS_IN_AN_HOUR
        for (i in 0..10)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(80 - i, startTimeMillis + 20L * i))

        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(defaultNumberOfHoursTracked)
        assert(result.size == BATTERY_LEVEL_NUMBER_OF_SAMPLES + 1)

        val firstResult = result.first()
        // first result should be the one added right after the 5 records added "two days before"
        assert(firstResult.level == 80)
    }

    @Test
    fun `returned records are filtered - last 6 hours`() {
        var startTimeMillis = Calendar.getInstance().timeInMillis - MILLIS_IN_A_DAY

        for (i in 0..4)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(90 - i, startTimeMillis + 20L * i))

        startTimeMillis += MILLIS_IN_AN_HOUR * 18L + 5L * MILLIS_IN_A_MINUTE
        for (i in 0..10)
            BatteryLevelTracker.addRecord(BatteryLevelInfo(70 - i, startTimeMillis + 20L * i))

        val result = BatteryLevelTracker.getRecordsAtFixedTimeInterval(customNumberOfHoursTracked)
        assert(result.size == BATTERY_LEVEL_NUMBER_OF_SAMPLES + 1)

        val firstResult = result.first()
        // first result should be the one added right after the 5 records added "24 hours before"
        assert(firstResult.level == 70)
    }

}
