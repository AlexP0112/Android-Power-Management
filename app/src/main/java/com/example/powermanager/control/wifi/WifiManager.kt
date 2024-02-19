package com.example.powermanager.control.wifi

import com.example.powermanager.utils.DISABLE_INTERFACE_COMMAND
import com.example.powermanager.utils.ENABLE_INTERFACE_COMMAND
import com.example.powermanager.utils.GET_ALL_WIFI_INTERFACES_COMMAND
import com.example.powermanager.utils.getInterfacesFromIfConfigOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object WifiManager {

    suspend fun turnWifiOffOrOn(on : Boolean) {
        withContext(Dispatchers.IO) {
            val command = if (on) ENABLE_INTERFACE_COMMAND else DISABLE_INTERFACE_COMMAND
            val interfaces = getAllWifiInterfacesNames()

            interfaces.forEach { interfaceName ->
                val process = Runtime.getRuntime().exec(String.format(command, interfaceName))
                process.waitFor()
            }
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