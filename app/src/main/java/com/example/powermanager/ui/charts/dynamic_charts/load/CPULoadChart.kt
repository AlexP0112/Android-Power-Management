package com.example.powermanager.ui.charts.dynamic_charts.load

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.powermanager.R
import com.example.powermanager.ui.charts.dynamic_charts.core.DynamicChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun CPULoadChart(
    model: PowerManagerAppModel
) {
    DynamicChart(
        inputDataFlow = model.cpuLoadFlow,
        chartLineColor = MaterialTheme.colorScheme.onSecondaryContainer,
        chartYAxisName = stringResource(R.string.load),
        customAxisValuesOverrider = null
    )
}
