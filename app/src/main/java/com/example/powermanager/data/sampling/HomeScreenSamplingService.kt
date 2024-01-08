package com.example.powermanager.data.sampling

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.os.BatteryManager
import android.os.PowerManager
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.state.HomeScreenInfo
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_SAMPLING_RATE_MILLIS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

object HomeScreenSamplingService {
    @SuppressLint("NewApi")
    fun startSampling(
        activityManager: ActivityManager,
        powerManager: PowerManager,
        batteryManager: BatteryManager,
        model: AppModel
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                // check if sampling should finish (current screen is not home screen)
                if (model.uiState.value.currentScreenName != HOME_SCREEN_NAME)
                    break

                // battery info
                val currentBatteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                val chargeOrDischargePrediction: Duration? = if (!batteryManager.isCharging) {
                    powerManager.batteryDischargePrediction
                } else {
                    val chargeTimeRemainingMillis = batteryManager.computeChargeTimeRemaining()
                    if (chargeTimeRemainingMillis == -1L) null else Duration.ofMillis(chargeTimeRemainingMillis)
                }

                // save the data that was gathered
                model.saveHomeScreenInfo(HomeScreenInfo(
                    isBatteryCharging = batteryManager.isCharging,
                    currentBatteryLevel = currentBatteryLevel,
                    chargeOrDischargePrediction = chargeOrDischargePrediction
                ))

                // sleep until next sampling cycle
                delay(HOME_SCREEN_SAMPLING_RATE_MILLIS)
            }
        }
    }
}