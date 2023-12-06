package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.R
import com.example.powermanager.ui.model.BatteryLevelTracker
import com.example.powermanager.utils.getHourAndMinuteFromLongTimestamp
import com.example.powermanager.utils.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    topPadding: Dp
) {
    Column(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.battery_level_chart_name)
            )
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        BatteryLevelChart()
    }
}

@Composable
fun BatteryLevelChart() {
    val records = BatteryLevelTracker.getRecordsAtFixedTimeInterval()

    val modelProducer = remember { ChartEntryModelProducer() }
    val datasetForModel = remember { mutableStateListOf(listOf<FloatEntry>()) }
    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    val scrollState = rememberChartScrollState()

    datasetLineSpec.add(
        LineChart.LineSpec(
            lineColor = MaterialTheme.colorScheme.tertiary.toArgb(),
            lineBackgroundShader = DynamicShaders.fromBrush(
                // vertical color gradient under the chart line
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.tertiary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                        MaterialTheme.colorScheme.tertiary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                    )
                )
            )
        )
    )

    // map the records to points on the chart
    val dataPoints = records.mapIndexed { index, record ->
        FloatEntry(index.toFloat(), record.level.toFloat())
    }

    datasetForModel.add(dataPoints)
    modelProducer.setEntries(datasetForModel)

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        ProvideChartStyle (
            chartStyle = m3ChartStyle()
        ){
            val marker = rememberMarker()
            Chart(
                chart = lineChart(
                    lines = datasetLineSpec
                ),
                startAxis = rememberStartAxis(
                    title = "Battery Percentage",
                    tickLength = 0.dp,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 10
                    ),
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Time",
                    valueFormatter = {value, _ ->
                        getHourAndMinuteFromLongTimestamp(timestamp = records[value.roundToInt()].timestamp)
                    }
                ),
                marker = marker,
                chartModelProducer = modelProducer,
                chartScrollState = scrollState,
                isZoomEnabled = true,
            )
        }
    }
}