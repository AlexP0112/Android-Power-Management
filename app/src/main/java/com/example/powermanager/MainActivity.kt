package com.example.powermanager

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.model.BatteryBroadcastReceiver
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.theme.PowerManagerTheme

class MainActivity : ComponentActivity() {
    private lateinit var appModel: AppModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appModel = AppModel(applicationContext)
        applicationContext.registerReceiver(BatteryBroadcastReceiver(), IntentFilter("android.intent.action.BATTERY_CHANGED"))
        setContent {
            PowerManagerTheme {
                PowerManagerApp(
                    context = applicationContext,
                    model = appModel
                )
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
            appModel.onEnterStatisticScreen(applicationContext)
    }
}
