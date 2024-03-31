package com.example.powermanager.control.wifi

import com.example.powermanager.utils.DISABLE_INTERFACE_COMMAND
import com.example.powermanager.utils.ENABLE_INTERFACE_COMMAND
import com.example.powermanager.utils.GET_ALL_WIFI_INTERFACES_COMMAND
import com.example.powermanager.utils.LinuxCommandsUtils.getInterfacesFromIfConfigOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object WiFiManager {

    var interfacesTurnedOff = false

    // function that indicates whether the automatic WiFi disabling feature is enabled by the user
    private var automaticTurnOffEnabled : () -> Boolean = { false }

    fun initialize(featureEnabled : () -> Boolean) {
        automaticTurnOffEnabled = featureEnabled
    }

    fun isAutomaticWifiDisablingFeatureEnabled() : Boolean {
        return automaticTurnOffEnabled()
    }

    // function that disables/enables WiFi interfaces (it does not change the actual
    // system setting for WiFi, as third party apps cannot access that setting)
    suspend fun turnWifiOffOrOn(turnOn : Boolean) {
        withContext(Dispatchers.IO) {
            val command = if (turnOn) ENABLE_INTERFACE_COMMAND else DISABLE_INTERFACE_COMMAND
            val interfaces = getAllWifiInterfacesNames()

            interfaces.forEach { interfaceName ->
                val process = Runtime.getRuntime().exec(String.format(command, interfaceName))
                process.waitFor()
            }

            interfacesTurnedOff = !turnOn
        }
    }

    // returns a list like ["wlan0", "wlan1"]
    private fun getAllWifiInterfacesNames() : List<String> {
        val process = Runtime.getRuntime().exec(GET_ALL_WIFI_INTERFACES_COMMAND)
        val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
        process.waitFor()

        return getInterfacesFromIfConfigOutput(output)
    }
}
