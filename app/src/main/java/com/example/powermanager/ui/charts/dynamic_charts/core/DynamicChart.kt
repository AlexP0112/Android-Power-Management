package com.example.powermanager.ui.charts.dynamic_charts.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.powermanager.ui.charts.utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.utils.getLineSpecsFromColors
import com.example.powermanager.ui.charts.utils.rememberMarker
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DynamicChart(
    inputDataFlow: StateFlow<MutableList<Float>>,
    chartLineColor: Color,
    chartYAxisName: String,
    customAxisValuesOverrider: CustomAxisValuesOverrider?
) {
    // retrieve the records from the flow
    val inputDataState = inputDataFlow.collectAsStateWithLifecycle(initialValue = mutableListOf())

    val modelProducer = remember { ChartEntryModelProducer() }
    val scrollState = rememberChartScrollState()

    val datasetLineSpec = getLineSpecsFromColors(listOf(chartLineColor))

    // map the records to points on the chart
    val dataPoints = inputDataState.value.mapIndexed { index, value ->
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
                chart = if (customAxisValuesOverrider != null) lineChart(
                    lines = datasetLineSpec,
                    axisValuesOverrider = remember {customAxisValuesOverrider}
                ) else lineChart(
                    lines = datasetLineSpec
                ),
                startAxis = rememberStartAxis(
                    title = chartYAxisName,
                    tickLength = 0.dp,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 10
                    ),
                    verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Center
                ),
                bottomAxis = rememberBottomAxis(
                    title = "Time",
                    valueFormatter = { _, _ -> ""}
                ),
                marker = marker,
                chartModelProducer = modelProducer,
                chartScrollState = scrollState,
                isZoomEnabled = true,
            )
        }
    }
}
