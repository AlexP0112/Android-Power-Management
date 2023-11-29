package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import com.example.powermanager.R
import com.example.powermanager.ui.model.BatteryLevelTracker

@Composable
fun StatisticsScreen(
    topPadding: Dp
) {
    Column(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.battery_level_chart_name)
        )
        BatteryLevelChart()
    }
}

@Composable
fun BatteryLevelChart() {
    val records = BatteryLevelTracker.getRecordsAtSamplingRate(2)

    val lineChartData = LineChartData(
        linePlotData =  LinePlotData(
            lines = listOf()
        ),
        gridLines = GridLines(color = MaterialTheme.colorScheme.outline),
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}