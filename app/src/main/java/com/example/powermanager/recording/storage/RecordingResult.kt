package com.example.powermanager.recording.storage

data class RecordingResult(
    val sessionName : String,
    val timestamp: String,
    val samplingPeriodMillis : Long,
    val numberOfSamples: Int,
    val batteryChargeValues : List<Int>,
    val batteryTemperatureValues : List<Float>,
    val memoryUsedValues : List<Float>,
    val cpuLoadValues : List<Float>,
    val peakMemoryUsed : Float,
    val averageMemoryUsed : Float,
    val peakCpuLoad : Float,
    val averageCpuLoad : Float,
    val averageBatteryTemperature : Float,
    val peakBatteryTemperature : Float,
    val numberOfBytesReceived : Long,
    val numberOfBytesSent : Long,
    val numberOfThreadsValues : List<Int>
)
