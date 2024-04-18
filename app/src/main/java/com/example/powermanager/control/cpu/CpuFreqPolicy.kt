package com.example.powermanager.control.cpu

data class CpuFreqPolicy(
    val name : String,
    val relatedCores : List<Int>,
    val frequenciesMhz : List<Int>,
    val maximumFrequencyGhz : Float
)
