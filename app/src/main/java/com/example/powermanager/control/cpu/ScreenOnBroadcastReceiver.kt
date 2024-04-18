package com.example.powermanager.control.cpu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenOnBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_SCREEN_ON == intent?.action)
            CpuFreqManager.resetFrequencyLimitsOnScreenOn()
    }
}
