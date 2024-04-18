package com.example.powermanager.ui.state

import com.example.powermanager.utils.NO_VALUE_STRING

data class ControlScreenUiState (
    val currentScalingGovernor : String,
    val currentCpuIdleGovernor : String,
    val disabledCores : List<Int>,
    val savedConfigurations : List<String>,
    val currentlySelectedCpuConfiguration : String = NO_VALUE_STRING,
    val policyToFrequencyLimitMHz : Map<String, Int>
)
