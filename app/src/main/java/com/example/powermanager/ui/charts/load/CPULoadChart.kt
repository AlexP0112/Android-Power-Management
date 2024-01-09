package com.example.powermanager.ui.charts.load

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.powermanager.R
import com.example.powermanager.ui.charts.common.SixtySecondsChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun CPULoadChart(
    model: PowerManagerAppModel
) {
    SixtySecondsChart(
        inputDataFlow = model.cpuLoadFlow,
        chartLineColor = MaterialTheme.colorScheme.onSecondaryContainer,
        chartYAxisName = stringResource(R.string.load),
        customAxisValuesOverrider = null
    )
}