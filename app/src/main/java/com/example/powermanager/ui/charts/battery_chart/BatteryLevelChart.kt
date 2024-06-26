package com.example.powermanager.ui.charts.battery_chart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.powermanager.data.battery.BatteryLevelTracker
import com.example.powermanager.ui.charts.utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.utils.rememberMarker
import com.example.powermanager.utils.FormattingUtils.getHourAndMinuteFromLongTimestamp
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
fun BatteryLevelChart(
    numberOfHoursTracked: Long,
    refreshChart : MutableState<Boolean>
) {
    // retrieve the records from the battery tracker
    val records = BatteryLevelTracker.getRecordsAtFixedTimeInterval(numberOfHoursTracked)

    val modelProducer = remember { ChartEntryModelProducer() }
    val scrollState = rememberChartScrollState()

    // for refresh purposes
    LaunchedEffect(refreshChart.value) {}

    val datasetLineSpec = listOf(
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
    modelProducer.setEntries(listOf(dataPoints))

    if (dataPoints.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            ProvideChartStyle(
                chartStyle = m3ChartStyle()
            ) {
                val marker = rememberMarker()
                Chart(
                    chart = lineChart(
                        lines = datasetLineSpec,
                        axisValuesOverrider = remember { CustomAxisValuesOverrider(0f, 100f) }
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
                        valueFormatter = { value, _ ->
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
}
