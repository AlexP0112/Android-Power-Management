package com.example.powermanager.control.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.PowerManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeviceIdleModeChangedBroadcastReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {

        val powerManager = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (powerManager.isDeviceIdleMode) {
            // device entered idle mode

            if (wifiManager.isWifiEnabled && !WiFiManager.turnedOff) {
                GlobalScope.launch {
                    WiFiManager.turnWifiOffOrOn(false)
                }
            }
        } else {
            // device exited idle mode

            if (WiFiManager.turnedOff) {
                GlobalScope.launch {
                    WiFiManager.turnWifiOffOrOn(true)
                }
            }
        }

    }
}
