package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.R
import com.example.powermanager.recording.storage.RecordingResult
import com.example.powermanager.ui.charts.static_charts.SingleLineStaticChart
import com.example.powermanager.ui.charts.utils.CustomAxisValuesOverrider
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.utils.FormattingUtils.getPrettyStringFromNumberOfBytes
import com.example.powermanager.utils.ListUtils.getListMaximum
import com.example.powermanager.utils.ListUtils.getListMinimum

@Composable
fun RecordingResultViewScreen(
    topPadding : Dp,
    model : PowerManagerAppModel,
    openRecordingResultsComparisonScreen : () -> Unit
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
        val result: RecordingResult = model.getCurrentlySelectedRecordingResult()
        var isCompareSectionExpanded by rememberSaveable { mutableStateOf(false) }

        val totalMemoryGB = model.getTotalMemory()
        val otherRecordingResults = uiState.recordingResults.filter { it != uiState.currentlySelectedRecordingResult }
        val batteryChargeValuesFloat = result.batteryChargeValues.map { it.toFloat() }
        val numberOfThreadsValuesFloat = result.numberOfThreadsValues.map { it.toFloat() }
        val batteryDischarge = result.batteryChargeValues.first() - result.batteryChargeValues.last()

        // title of the screen
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Recording session '${uiState.currentlySelectedRecordingResult}'",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // general info about the recording session
        Text(
            text = "\u25cb Timestamp: ${result.timestamp}"
        )

        Text(
            text = "\u25cb Number of samples: ${result.numberOfSamples}"
        )

        Text(
            text = "\u25cb Time interval between samples: ${result.samplingPeriodMillis} ms"
        )

        Text(
            text = "\u25cb Thread count information included: ${if (numberOfThreadsValuesFloat.isEmpty()) "no" else "yes"}"
        )

        Text(text = "\u25cb Charging status during session: ${if (batteryDischarge >= 0) "not charging" else "charging"}")

        Spacer(modifier = Modifier.height(6.dp))

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // cpu, memory and battery stats

        Text(
            text = if (batteryDischarge >= 0) "\u25cb Total battery discharge: $batteryDischarge mAh" else
                "\u25cb Total battery charge: ${-batteryDischarge} mAh"
        )

        Text(
            text = "\u25cb Average battery temperature: ${String.format("%.1f \u00b0C", result.averageBatteryTemperature)}"
        )

        Text(
            text = "\u25cb Peak battery temperature: ${String.format("%.1f \u00b0C", result.peakBatteryTemperature)}"
        )

        Text(
            text = "\u25cb Average memory usage: ${String.format("%.2f", result.averageMemoryUsed)}/${String.format("%.2f", totalMemoryGB)}GB (${String.format("%.1f", result.averageMemoryUsed * 100f / totalMemoryGB)}%)"
        )

        Text(
            text = "\u25cb Peak memory usage: ${String.format("%.2f", result.peakMemoryUsed)}/${String.format("%.2f", totalMemoryGB)}GB (${String.format("%.1f", result.peakMemoryUsed * 100f / totalMemoryGB)}%)"
        )

        Text(
            text = "\u25cb Average CPU load: ${String.format("%.2f", result.averageCpuLoad)}"
        )

        Text(
            text = "\u25cb Peak CPU load: ${String.format("%.2f", result.peakCpuLoad)}"
        )

        Spacer(modifier = Modifier.height(6.dp))

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // network stats
        Text(
            text = "\u25cb Total internet traffic (download): ${getPrettyStringFromNumberOfBytes(result.numberOfBytesReceived)}"
        )

        Text(
            text = "\u25cb Total internet traffic (upload): ${getPrettyStringFromNumberOfBytes(result.numberOfBytesSent)}"
        )

        Spacer(modifier = Modifier.height(6.dp))

        Divider(
            modifier = Modifier
                .fillMaxSize(),
            thickness = 0.75.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // charts

        // battery level chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.battery_level_mah)
        )

        SingleLineStaticChart(
            chartLineColor = MaterialTheme.colorScheme.tertiary,
            inputData = batteryChargeValuesFloat,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(batteryChargeValuesFloat) - 1f,
                maxYValue = getListMaximum(batteryChargeValuesFloat) + 1f
            )
        )

        // battery temperature chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.battery_temperature_c)
        )

        SingleLineStaticChart(
            chartLineColor = MaterialTheme.colorScheme.primary,
            inputData = result.batteryTemperatureValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(result.batteryTemperatureValues) - 0.5f,
                maxYValue = result.peakBatteryTemperature + 0.5f
            )
        )

        // memory chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.memory_usage_gb)
        )

        SingleLineStaticChart(
            chartLineColor = MaterialTheme.colorScheme.secondary,
            inputData = result.memoryUsedValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(result.memoryUsedValues) - 0.01f,
                maxYValue = result.peakMemoryUsed + 0.01f
            )
        )

        // CPU load chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.cpu_load)
        )

        SingleLineStaticChart(
            chartLineColor = MaterialTheme.colorScheme.onSecondaryContainer,
            inputData = result.cpuLoadValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(result.cpuLoadValues) - 0.01f,
                maxYValue = result.peakCpuLoad + 0.01f
            )
        )

        // number of threads chart, if the stat was included
        if (numberOfThreadsValuesFloat.isNotEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.total_number_of_threads)
            )

            SingleLineStaticChart(
                chartLineColor = MaterialTheme.colorScheme.primary,
                inputData = numberOfThreadsValuesFloat,
                customAxisValuesOverrider = CustomAxisValuesOverrider(
                    minYValue = getListMinimum(numberOfThreadsValuesFloat) - 1f,
                    maxYValue = getListMaximum(numberOfThreadsValuesFloat) + 1f
                )
            )
        }

        // button that expands or collapses the comparison section
        CompareResultHeaderRow(
            isExpanded = isCompareSectionExpanded
        ) {
            isCompareSectionExpanded = !isCompareSectionExpanded
        }

        // list of available results to compare with, with a button for each result
        // that takes you to the comparison screen
        // this list is visible only when the section is expanded
        if (isCompareSectionExpanded && otherRecordingResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(5.dp))

            otherRecordingResults.forEach { recordingName ->
                Divider(
                    modifier = Modifier
                        .fillMaxSize(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                )

                RecordingResultToCompareWithRow(
                    name = recordingName
                ) {
                    model.selectRecordingResultForComparison(recordingName)
                    openRecordingResultsComparisonScreen()
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun CompareResultHeaderRow(
    isExpanded : Boolean,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.compare_result)
        )

        IconButton(onClick = onClick) {
            Icon(
                imageVector = if (!isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = null
            )
        }
    }
}

@Composable
fun RecordingResultToCompareWithRow(
    name : String,
    onSelected : () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name
        )

        OutlinedButton(
            onClick = onSelected
        ) {
            Text(text = stringResource(R.string.compare))
        }
    }
}
