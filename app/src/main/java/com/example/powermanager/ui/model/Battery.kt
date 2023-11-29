package com.example.powermanager.ui.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Calendar
import kotlin.math.roundToInt

data class BatteryLevelInfo(
    val level: Int,
    val hour: Int,
    val minute: Int,
)

class BatteryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val batteryLevel: Int? = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val batteryScale: Int? = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (batteryLevel == null || batteryLevel == -1 || batteryScale == null || batteryLevel == -1)
            return

        val percentage = ((batteryLevel * 100) / batteryScale.toFloat()).roundToInt()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)

        BatteryLevelTracker.addRecord(BatteryLevelInfo(percentage, hour, minute))
    }
}

object BatteryLevelTracker {
    private var records: MutableList<BatteryLevelInfo> = mutableListOf()

    fun addRecord(info: BatteryLevelInfo) {
        records.add(info)
    }

    fun getRecords(): MutableList<BatteryLevelInfo> {
        return records
    }

    fun clear() {
        records.clear()
    }
}