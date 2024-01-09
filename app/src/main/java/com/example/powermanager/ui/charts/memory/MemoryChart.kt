package com.example.powermanager.ui.charts.memory

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.powermanager.ui.charts.charts_utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.charts_utils.rememberMarker
import com.example.powermanager.ui.model.PowerManagerAppModel
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
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun MemoryChart(
    totalMemoryGB: Float,
    model: PowerManagerAppModel
) {
    // retrieve the records from the flow
    val memoryUsageState = model.memoryUsageFlow.collectAsStateWithLifecycle(initialValue = mutableListOf())

    val modelProducer = remember { ChartEntryModelProducer() }
    val scrollState = rememberChartScrollState()

    val datasetLineSpec = listOf(
        LineChart.LineSpec(
            lineColor = MaterialTheme.colorScheme.secondary.toArgb(),
            lineBackgroundShader = DynamicShaders.fromBrush(
                // vertical color gradient under the chart line
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                        MaterialTheme.colorScheme.secondary.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                    )
                )
            )
        )
    )

    // map the records to points on the chart
    val dataPoints = memoryUsageState.value.mapIndexed { index, value ->
        FloatEntry(index.toFloat(), value)
    }
    modelProducer.setEntries(listOf(dataPoints))

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
                    lines = datasetLineSpec,
                    axisValuesOverrider = remember { CustomAxisValuesOverrider(0f, totalMemoryGB) }
                ),
                startAxis = rememberStartAxis(
                    title = "Memory (GB)",
                    tickLength = 0.dp,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 10
                    ),
                    verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Center
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Time",
                    valueFormatter = { _, _ -> ""} // bottom axis should have no labels (time is implicit)
                ),
                marker = marker,
                chartModelProducer = modelProducer,
                chartScrollState = scrollState,
                isZoomEnabled = true,
            )
        }
    }
}