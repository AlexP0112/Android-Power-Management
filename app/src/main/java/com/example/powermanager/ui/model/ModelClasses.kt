package com.example.powermanager.ui.model

import com.example.powermanager.utils.NO_VALUE_STRING

enum class FlowType {
    MEMORY,
    FREQUENCY,
    LOAD
}

data class HomeScreenInfo(
    val batteryStatus : String = NO_VALUE_STRING,
    val currentBatteryLevel : Int = 0,
    val batteryChargeCount : Int = 0,
    val batteryVoltageString : String = NO_VALUE_STRING,
    val batteryCurrentString : String = NO_VALUE_STRING,
    val batteryHealthString : String = NO_VALUE_STRING,
    val batteryCyclesString : String = NO_VALUE_STRING,
    val powerSaveState : Boolean = false,
    val batteryTemperatureString : String = NO_VALUE_STRING,
    val lowPowerStandbyEnabled : Boolean = false,
    val usedMemoryGB : Float = 0f,
    val cpuLoad : Float = 0f,
    val cpuFrequenciesGHz : List<Float> = listOf(),
    val systemUptimeString : String = NO_VALUE_STRING,
    val numberOfProcesses : Int = 0,
    val numberOfThreads : Int = 0,
    val scalingGovernor : String = NO_VALUE_STRING,
    val onlineCores : List<Int> = listOf()
)

data class FlowSample(
    val value : Float = 0f,
    val timestamp : Long = 0L
)
