package com.example.powermanager.control.cpu.storage

data class CpuConfiguration(
    val name: String,
    val onlineCores: List<Int>,
    val scalingGovernor: String,
    val cpuIdleGovernor: String,
    val policyToFrequencyLimitMHz: Map<String, Int>
)
