package com.example.powermanager.data.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Calendar
import kotlin.math.roundToInt

class BatteryChangedBroadcastReceiver : BroadcastReceiver() {
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
