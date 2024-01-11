package com.example.powermanager.ui.charts.memory

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.powermanager.R
import com.example.powermanager.ui.charts.common.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.common.FlowBasedChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun MemoryChart(
    model: PowerManagerAppModel
) {
    FlowBasedChart(
        inputDataFlow = model.memoryUsageFlow,
        chartLineColor = MaterialTheme.colorScheme.secondary,
        chartYAxisName = stringResource(R.string.memory_gb),
        customAxisValuesOverrider = CustomAxisValuesOverrider(0f, model.getTotalMemory())
    )
}