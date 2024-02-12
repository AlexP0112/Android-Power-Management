package com.example.powermanager.recording.model

data class RecordingResult(
    val sessionName : String,
    val timestamp: String,
    val duration: String,
    val numberOfSamples: Int,
    val batteryChargeValues : List<Int>,
    val memoryUsedValues : List<Float>,
    val cpuLoadValues : List<Float>,
    val peakMemoryUsed : Float,
    val averageMemoryUsed : Float,
    val peakCpuLoad : Float,
    val averageCpuLoad : Float
)
