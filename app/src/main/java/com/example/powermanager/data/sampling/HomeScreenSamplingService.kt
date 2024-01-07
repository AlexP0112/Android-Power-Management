package com.example.powermanager.data.sampling

import android.app.ActivityManager
import android.os.BatteryManager
import android.os.PowerManager
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_SAMPLING_RATE_MILLIS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration

object HomeScreenSamplingService {
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

                // battery charge/discharge prediction
                var prediction: Duration?
                if (!batteryManager.isCharging) {
                    prediction = powerManager.batteryDischargePrediction
                } else {
                    val chargeTimeRemainingMillis = batteryManager.computeChargeTimeRemaining()
                    prediction = if (chargeTimeRemainingMillis == -1L) null else Duration.ofMillis(chargeTimeRemainingMillis)
                }

                withContext(Dispatchers.Main) {
                    model.saveChargeOrDischargePrediction(prediction)
                }

                // sleep until next sampling cycle
                delay(HOME_SCREEN_SAMPLING_RATE_MILLIS)
            }
        }
    }
}