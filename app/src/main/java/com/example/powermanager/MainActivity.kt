package com.example.powermanager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.powermanager.data.sampling.BatteryBroadcastReceiver
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.theme.PowerManagerTheme

class MainActivity : ComponentActivity() {
    private lateinit var appModel: AppModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appModel = AppModel(
            am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager,
            pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager,
            bm = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        )

        applicationContext.registerReceiver(BatteryBroadcastReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        appModel.onEnterApp()

        setContent {
            PowerManagerTheme {
                PowerManagerApp(model = appModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appModel.onLeaveApp()
    }

    override fun onRestart() {
        super.onRestart()
        appModel.onEnterApp()
    }
}
