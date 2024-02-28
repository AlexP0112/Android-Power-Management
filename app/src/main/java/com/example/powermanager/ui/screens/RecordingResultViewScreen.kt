package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.R
import com.example.powermanager.recording.model.RecordingResult
import com.example.powermanager.ui.charts.common.CustomAxisValuesOverrider
import com.example.powermanager.ui.charts.common.StaticChart
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.getListMaximum
import com.example.powermanager.utils.getListMinimum

@Composable
fun RecordingResultViewScreen(
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
        val uiState: State<AppUiState> = model.uiState.collectAsState()
        val result: RecordingResult = model.getCurrentlySelectedRecordingResult()

        val totalMemoryGB = model.getTotalMemory()
        val batteryChargeValuesFloat = result.batteryChargeValues.map { it.toFloat() }
        val numberOfThreadsValuesFloat = result.numberOfThreadsValues.map { it.toFloat() }
        val batteryDischarge = result.batteryChargeValues.first() - result.batteryChargeValues.last()

        // title of the screen
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Recording session '${uiState.value.currentlySelectedRecordingResult}'",
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

        // charts
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.battery_level_mah)
        )

        // battery level chart
        StaticChart(
            chartLineColor = MaterialTheme.colorScheme.tertiary,
            inputData = batteryChargeValuesFloat,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(batteryChargeValuesFloat) - 1f,
                maxYValue = getListMaximum(batteryChargeValuesFloat) + 1f
            )
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.memory_usage_gb)
        )

        // memory chart
        StaticChart(
            chartLineColor = MaterialTheme.colorScheme.secondary,
            inputData = result.memoryUsedValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(result.memoryUsedValues) - 0.01f,
                maxYValue = result.peakMemoryUsed + 0.01f
            )
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.cpu_load)
        )

        // CPU load chart
        StaticChart(
            chartLineColor = MaterialTheme.colorScheme.onSecondaryContainer,
            inputData = result.cpuLoadValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(result.cpuLoadValues) - 0.01f,
                maxYValue = result.peakCpuLoad + 0.01f
            )
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.total_number_of_threads)
        )

        // number of threads chart
        StaticChart(
            chartLineColor = MaterialTheme.colorScheme.primary,
            inputData = numberOfThreadsValuesFloat,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = getListMinimum(numberOfThreadsValuesFloat) - 1f,
                maxYValue = getListMaximum(numberOfThreadsValuesFloat) + 1f
            )
        )
    }
}
