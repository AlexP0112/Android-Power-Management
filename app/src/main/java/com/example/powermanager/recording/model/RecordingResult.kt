package com.example.powermanager.recording.model

data class RecordingResult(
    val sessionName : String,
    val timestamp: String,
    val samplingPeriodMillis : Long,
    val numberOfSamples: Int,
    val batteryChargeValues : List<Int>,
    val memoryUsedValues : List<Float>,
    val cpuLoadValues : List<Float>,
    val peakMemoryUsed : Float,
    val averageMemoryUsed : Float,
    val peakCpuLoad : Float,
    val averageCpuLoad : Float,
    val numberOfThreadsValues : List<Int>,
    val bytesRx : Long,
    val bytesTx : Long,
    val packetsRx : Long,
    val packetsTx : Long
)
