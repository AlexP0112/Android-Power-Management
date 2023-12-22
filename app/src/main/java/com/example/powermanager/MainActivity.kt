package com.example.powermanager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.powermanager.data.sampling.BatteryBroadcastReceiver
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.theme.PowerManagerTheme
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME

class MainActivity : ComponentActivity() {
    private lateinit var appModel: AppModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appModel = AppModel(applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        applicationContext.registerReceiver(BatteryBroadcastReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        setContent {
            PowerManagerTheme {
                PowerManagerApp(model = appModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (appModel.uiState.value.currentScreenName == STATISTICS_SCREEN_NAME)
            appModel.onLeaveStatisticsScreen()
    }

    override fun onRestart() {
        super.onRestart()

        if (appModel.uiState.value.currentScreenName == STATISTICS_SCREEN_NAME)
            appModel.onEnterStatisticScreen()
    }
}
