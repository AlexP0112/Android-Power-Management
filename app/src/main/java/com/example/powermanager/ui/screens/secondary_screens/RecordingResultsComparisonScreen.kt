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
import com.example.powermanager.utils.FormattingUtils.getPrettyStringFromNumberOfBytes
import com.example.powermanager.utils.ListUtils.getListMaximum
import com.example.powermanager.utils.ListUtils.getListMinimum
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
        val leftBatteryDischarge = leftResult.batteryChargeValues.first() - leftResult.batteryChargeValues.last()
        val rightBatteryDischarge = rightResult.batteryChargeValues.first() - rightResult.batteryChargeValues.last()

        // determine charts boundaries
        val minMemoryUsage = min(getListMinimum(leftResult.memoryUsedValues), getListMinimum(rightResult.memoryUsedValues)) - 0.02f
        val maxMemoryUsage = max(leftResult.peakMemoryUsed, rightResult.peakMemoryUsed) + 0.02f
        val minCpuLoad = min(getListMinimum(leftResult.cpuLoadValues), getListMinimum(rightResult.cpuLoadValues)) - 0.1f
        val maxCpuLoad = max(leftResult.peakCpuLoad, rightResult.peakCpuLoad) + 0.1f
        val minBatteryLevel = max(min(getListMinimum(leftBatteryValuesFloat), getListMinimum(rightBatteryValuesFloat)) - 50f, 0f)
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

        // ========== general info about the recording sessions ========== //

        Text(
            text = "\u25cb Number of samples: ${
                if (leftResult.numberOfSamples == rightResult.numberOfSamples) 
                    leftResult.numberOfSamples else
                    "${leftResult.numberOfSamples} vs ${rightResult.numberOfSamples}"}"
        )

        Text(
            text = "\u25cb Time between samples: ${
                if (leftResult.samplingPeriodMillis == rightResult.samplingPeriodMillis)
                    leftResult.samplingPeriodMillis else
                    "${leftResult.samplingPeriodMillis}ms vs ${rightResult.samplingPeriodMillis}ms"}"
        )

        Text(
            text = "\u25cb Thread count information included: ${
                if (leftResult.numberOfThreadsValues.isEmpty() == rightResult.numberOfThreadsValues.isEmpty())
                    (if (leftResult.numberOfThreadsValues.isEmpty()) "no" else "yes") else
                    "${if (leftResult.numberOfThreadsValues.isEmpty()) "no" else "yes"} vs ${if (rightResult.numberOfThreadsValues.isEmpty()) "no" else "yes"}"}"
        )

        Text(text = "\u25cb Charging status: ${
            if (leftBatteryDischarge * rightBatteryDischarge >= 0)
                (if (leftBatteryDischarge >= 0) "not charging" else "charging") else
                "${(if (leftBatteryDischarge >= 0) "not charging" else "charging")} vs ${(if (rightBatteryDischarge >= 0) "not charging" else "charging")}"}")

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ============ cpu, memory and battery stats ========= //

        Text(text = "\u25cb Total battery discharge: $leftBatteryDischarge mAh vs $rightBatteryDischarge mAh")

        Text(text = "\u25cb Average memory usage: ${String.format("%.2f", leftResult.averageMemoryUsed)} GB vs ${String.format("%.2f", rightResult.averageMemoryUsed)} GB")

        Text(text = "\u25cb Peak memory usage: ${String.format("%.2f", leftResult.peakMemoryUsed)} GB vs ${String.format("%.2f", rightResult.peakMemoryUsed)} GB")

        Text(text = "\u25cb Average CPU load: ${String.format("%.2f", leftResult.averageCpuLoad)} vs ${String.format("%.2f", rightResult.averageCpuLoad)}")

        Text(text = "\u25cb Peak CPU load: ${String.format("%.2f", leftResult.peakCpuLoad)} vs ${String.format("%.2f", rightResult.peakCpuLoad)}")

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ================== network stats ================== //

        Text(
            text = "\u25cb Total internet traffic (DL): ${getPrettyStringFromNumberOfBytes(leftResult.numberOfBytesReceived)} vs ${getPrettyStringFromNumberOfBytes(rightResult.numberOfBytesReceived)}"
        )

        Text(
            text = "\u25cb Total internet traffic (UL): ${getPrettyStringFromNumberOfBytes(leftResult.numberOfBytesSent)} vs ${getPrettyStringFromNumberOfBytes(rightResult.numberOfBytesSent)}"
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
