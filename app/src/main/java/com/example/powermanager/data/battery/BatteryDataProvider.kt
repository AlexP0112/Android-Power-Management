package com.example.powermanager.data.battery

import com.example.powermanager.utils.BATTERY_CURRENT_PATH
import com.example.powermanager.utils.BATTERY_CYCLES_PATH
import com.example.powermanager.utils.BATTERY_HEALTH_PATH
import com.example.powermanager.utils.BATTERY_STATUS_PATH
import com.example.powermanager.utils.BATTERY_TEMPERATURE_PATH
import com.example.powermanager.utils.BATTERY_VOLTAGE_PATH
import com.example.powermanager.utils.NUMBER_OF_MICROS_IN_AN_UNIT
import com.example.powermanager.utils.NUMBER_OF_MICROS_IN_A_MILLI
import com.example.powermanager.utils.readProtectedFileContent

object BatteryDataProvider {

    fun getBatteryStatus() : String {
        return readProtectedFileContent(BATTERY_STATUS_PATH).trim()
    }

    fun getBatteryTemperature() : String {
        val fileContent = readProtectedFileContent(BATTERY_TEMPERATURE_PATH).trim()
        val temperatureFloat = fileContent.toFloat() / 10f

        return "$temperatureFloat \u00b0C"
    }

    fun getBatteryVoltage() : String {
        val voltageMicroVolts = readProtectedFileContent(BATTERY_VOLTAGE_PATH).trim()

        return String.format("%.3f V", voltageMicroVolts.toFloat() / NUMBER_OF_MICROS_IN_AN_UNIT.toFloat())
    }

    fun getBatteryCurrent() : String {
        val currentMicroAmps = readProtectedFileContent(BATTERY_CURRENT_PATH).trim()

        return String.format("%.1f mA", currentMicroAmps.toFloat() / NUMBER_OF_MICROS_IN_A_MILLI.toFloat())
    }

    fun getBatteryHealth() : String {
        return readProtectedFileContent(BATTERY_HEALTH_PATH).trim()
    }

    fun getBatteryCycles() : String {
        return readProtectedFileContent(BATTERY_CYCLES_PATH).trim()
    }

}
