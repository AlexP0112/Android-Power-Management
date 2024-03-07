package com.example.powermanager.ui.model

import com.example.powermanager.utils.NO_VALUE_STRING
import java.time.Duration

enum class FlowType {
    MEMORY,
    FREQUENCY,
    LOAD
}

data class HomeScreenInfo(
    val isBatteryCharging : Boolean = false,
    val currentBatteryLevel : Int = 0,
    val batteryChargeCount : Int = 0,
    val chargeOrDischargePrediction : Duration? = null,
    val powerSaveState : Boolean = false,
    val lowPowerStandbyEnabled : Boolean = false,
    val usedMemoryGB : Float = 0f,
    val cpuLoad : Float = 0f,
    val cpuFrequenciesGHz : List<Float> = listOf(),
    val systemUptimeString : String = NO_VALUE_STRING,
    val numberOfProcesses : Int = 0,
    val numberOfThreads : Int = 0,
    val onlineCores : List<Int> = listOf()
)

data class FlowSample(
    val value : Float = 0f,
    val timestamp : Long = 0L
)
