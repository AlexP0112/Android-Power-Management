package com.example.powermanager.control.storage

data class CpuConfiguration(
    val name: String,
    val onlineCores: List<Int>,
    val scalingGovernor: String,
    val policyToFrequencyLimitMHz: Map<String, Int>
)
