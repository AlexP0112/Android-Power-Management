package com.example.powermanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.powermanager.control.cpufreq.ScreenOnBroadcastReceiver
import com.example.powermanager.control.wifi.DeviceIdleModeChangedBroadcastReceiver
import com.example.powermanager.data.battery.BatteryChangedBroadcastReceiver
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.theme.PowerManagerTheme
import com.example.powermanager.utils.NOTIFICATION_CHANNEL_ID
import com.example.powermanager.utils.NOTIFICATION_CHANNEL_NAME

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBroadcastReceivers()
        createNotificationChannel()

        setContent {
            PowerManagerTheme {
                val viewModel = viewModel<PowerManagerAppModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return PowerManagerAppModel(
                                application = application
                            ) as T
                        }
                    }
                )

                PowerManagerApp(model = viewModel) {
                    startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                }
            }
        }
    }

    /* create a notification channel */
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun registerBroadcastReceivers() {
        // register receiver for battery broadcasts
        applicationContext.registerReceiver(BatteryChangedBroadcastReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // register receiver for device idle mode status change
        applicationContext.registerReceiver(DeviceIdleModeChangedBroadcastReceiver(), IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED))

        // register receiver for screen on broadcast
        applicationContext.registerReceiver(ScreenOnBroadcastReceiver(), IntentFilter(Intent.ACTION_SCREEN_ON))
    }

}
