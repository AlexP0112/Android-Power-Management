package com.example.powermanager.ui.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.utils.NO_VALUE_STRING

data class ControlScreenUiState (
    val currentScalingGovernor : String,
    val disabledCores : List<Int>,
    val savedConfigurations : List<String>,
    val currentlySelectedCpuConfiguration : String = NO_VALUE_STRING,
    val policyToFrequencyLimitMHz : Map<String, Int>,
    val cpuConfigurationName : String = "",
    val infoDialogTextId : Int? = null,
    val infoDialogHeightDp: Dp = 0.dp,
    val isConfirmConfigurationDeletionDialogOpen : Boolean = false,
    val isInspectConfigurationDialogOpen : Boolean = false
)