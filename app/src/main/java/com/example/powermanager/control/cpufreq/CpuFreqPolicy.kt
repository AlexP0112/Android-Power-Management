package com.example.powermanager.control.cpufreq

data class CpuFreqPolicy(
    val name : String,
    val affectedCores : List<Int>,
    val frequenciesMhz : List<Int>,
    val maximumFrequencyGhz : Float
)
