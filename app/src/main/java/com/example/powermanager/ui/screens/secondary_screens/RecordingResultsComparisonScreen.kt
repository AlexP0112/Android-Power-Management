package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.R
import com.example.powermanager.recording.storage.RecordingResult
import com.example.powermanager.ui.charts.static_charts.DoubleLineStaticChart
import com.example.powermanager.ui.charts.utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.utils.getListMaximum
import com.example.powermanager.utils.getListMinimum
import kotlin.math.max
import kotlin.math.min

@Composable
fun RecordingResultsComparisonScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
) {
    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val uiState by model.recordingScreensUiState.collectAsState()

        // get the two results that are compared
        val leftResult: RecordingResult = model.getCurrentlySelectedRecordingResult()
        val rightResult: RecordingResult = model.getRecordingResultToCompareWith()

        // convert integer lists to float lists, as required for charts
        val leftBatteryValuesFloat = leftResult.batteryChargeValues.map {it.toFloat()}
        val rightBatteryValuesFloat = rightResult.batteryChargeValues.map { it.toFloat() }
        val leftThreadCountValuesFloat = leftResult.numberOfThreadsValues.map { it.toFloat() }
        val rightThreadCountValuesFloat = rightResult.numberOfThreadsValues.map { it.toFloat() }

        // determine charts boundaries
        val minMemoryUsage = min(getListMinimum(leftResult.memoryUsedValues), getListMinimum(rightResult.memoryUsedValues)) - 0.02f
        val maxMemoryUsage = max(leftResult.peakMemoryUsed, rightResult.peakMemoryUsed) + 0.02f
        val minCpuLoad = min(getListMinimum(leftResult.cpuLoadValues), getListMinimum(rightResult.cpuLoadValues)) - 0.1f
        val maxCpuLoad = max(leftResult.peakCpuLoad, rightResult.peakCpuLoad) + 0.1f
        val minBatteryLevel = max(min(getListMinimum(leftBatteryValuesFloat), getListMinimum(rightBatteryValuesFloat)) - 50, 0f)
        val maxBatteryLevel = max(getListMaximum(leftBatteryValuesFloat), getListMaximum(rightBatteryValuesFloat)) + 50f
        val minThreadCount = min(getListMinimum(leftThreadCountValuesFloat), getListMinimum(rightThreadCountValuesFloat)) - 50f
        val maxThreadCount = max(getListMaximum(leftThreadCountValuesFloat), getListMaximum(rightThreadCountValuesFloat)) + 50f

        // ====================== screen title ====================== //
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${uiState.currentlySelectedRecordingResult} vs ${uiState.selectedToCompareRecordingResult}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ====================== charts ====================== //

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // legend
        ChartsLegend(
            firstRecordingName = uiState.currentlySelectedRecordingResult,
            secondRecordingName = uiState.selectedToCompareRecordingResult
        )

        Spacer(modifier = Modifier.height(10.dp))

        // battery level chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.battery_level_mah)
        )

        DoubleLineStaticChart(
            firstInput = leftBatteryValuesFloat,
            secondInput = rightBatteryValuesFloat,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = minBatteryLevel,
                maxYValue = maxBatteryLevel
            )
        )

        // memory chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.memory_usage_gb)
        )

        DoubleLineStaticChart(
            firstInput = leftResult.memoryUsedValues,
            secondInput = rightResult.memoryUsedValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = minMemoryUsage,
                maxYValue = maxMemoryUsage
            )
        )

        // CPU load chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.cpu_load)
        )

        DoubleLineStaticChart(
            firstInput = leftResult.cpuLoadValues,
            secondInput = rightResult.cpuLoadValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = minCpuLoad,
                maxYValue = maxCpuLoad
            )
        )

        // number of threads chart, if the stat was included
        if (leftThreadCountValuesFloat.isNotEmpty() && rightThreadCountValuesFloat.isNotEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.total_number_of_threads)
            )

            DoubleLineStaticChart(
                firstInput = leftThreadCountValuesFloat,
                secondInput = rightThreadCountValuesFloat,
                customAxisValuesOverrider = CustomAxisValuesOverrider(
                    minYValue = minThreadCount,
                    maxYValue = maxThreadCount
                )
            )
        }
    }
}

@Composable
fun ChartsLegend(
    firstRecordingName: String,
    secondRecordingName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.charts_legend)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = R.drawable.horizontal_line),
                contentDescription = null,
                modifier = Modifier.scale(2f, 1f),
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = firstRecordingName
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = R.drawable.horizontal_line),
                modifier = Modifier.scale(2f, 1f),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = secondRecordingName
            )
        }
    }
}
