package com.example.powermanager.ui.charts.static_charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

/*
 * A chart that contains two lines, used for recording results comparisons
 */
@Composable
fun DoubleLineStaticChart(
    firstInput: List<Float>,
    secondInput: List<Float>,
    customAxisValuesOverrider: CustomAxisValuesOverrider
) {
    val modelProducer = remember { ChartEntryModelProducer() }
    val scrollState = rememberChartScrollState()

    val datasetLineSpecs = getLineSpecsFromColors(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary))

    // map the input data to points on the chart
    val firstLinePoints = firstInput.mapIndexed { index, value ->
        FloatEntry(index.toFloat(), value)
    }

    val secondLinePoints = secondInput.mapIndexed { index, value ->
        FloatEntry(index.toFloat(), value)
    }

    modelProducer.setEntries(listOf(firstLinePoints, secondLinePoints))

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
                    lines = datasetLineSpecs,
                    axisValuesOverrider = customAxisValuesOverrider
                ),
                startAxis = rememberStartAxis(
                    tickLength = 0.dp,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 10
                    ),
                    verticalLabelPosition = VerticalAxis.VerticalLabelPosition.Center
                ),
                bottomAxis = rememberBottomAxis(valueFormatter = { _, _ -> ""}),
                marker = marker,
                chartModelProducer = modelProducer,
                chartScrollState = scrollState,
                isZoomEnabled = true,
            )
        }
    }
}
