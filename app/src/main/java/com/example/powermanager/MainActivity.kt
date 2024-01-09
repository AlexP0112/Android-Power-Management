package com.example.powermanager

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.powermanager.data.sampling.BatteryBroadcastReceiver
import com.example.powermanager.ui.main.PowerManagerApp
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.theme.PowerManagerTheme

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationContext.registerReceiver(BatteryBroadcastReceiver(), IntentFilter(Intent.ACTION_BATTERY_CHANGED))

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

                PowerManagerApp(model = viewModel)
            }
        }
    }
}
