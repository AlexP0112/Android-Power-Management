package com.example.powermanager.ui.charts.dynamic_charts.memory

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.powermanager.R
import com.example.powermanager.ui.charts.utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.dynamic_charts.core.DynamicChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun MemoryChart(
    model: PowerManagerAppModel
) {
    DynamicChart(
        inputDataFlow = model.memoryUsageFlow,
        chartLineColor = MaterialTheme.colorScheme.secondary,
        chartYAxisName = stringResource(R.string.memory_gb),
        customAxisValuesOverrider = CustomAxisValuesOverrider(0f, model.getTotalMemory())
    )
}
