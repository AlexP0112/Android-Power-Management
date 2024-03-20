package com.example.powermanager.data.battery

object BatteryTemperatureTracker {

    private var onTemperatureValueReceived : (Float) -> Unit = {}

    fun changeTemperatureChangeCallback(onChange : (Float) -> Unit) {
        onTemperatureValueReceived = onChange
    }

    fun changeTemperature(temperature : Float) {
        onTemperatureValueReceived(temperature)
    }

}
