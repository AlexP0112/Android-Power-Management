package com.example.powermanager.ui.charts.frequency

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.powermanager.R
import com.example.powermanager.ui.charts.common.DynamicChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun CPUFrequencyChart(
    model: PowerManagerAppModel
) {
    DynamicChart(
        inputDataFlow = model.cpuFrequencyFlow,
        chartLineColor = MaterialTheme.colorScheme.primary,
        chartYAxisName = stringResource(R.string.frequency_ghz),
        customAxisValuesOverrider = null
    )
}
