package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        val leftResult: RecordingResult = model.getCurrentlySelectedRecordingResult()
        val rightResult: RecordingResult = model.getRecordingResultToCompareWith()

        // determine charts boundaries
        val minMemoryUsage = min(getListMinimum(leftResult.memoryUsedValues), getListMinimum(rightResult.memoryUsedValues))
        val maxMemoryUsage = max(leftResult.peakMemoryUsed, rightResult.peakMemoryUsed)

        // title of the screen
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${uiState.currentlySelectedRecordingResult} vs ${uiState.selectedToCompareRecordingResult}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ====================== charts ====================== //

        // memory chart
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(R.string.memory_usage_gb)
        )

        DoubleLineStaticChart(
            firstInput = leftResult.memoryUsedValues,
            secondInput = rightResult.memoryUsedValues,
            customAxisValuesOverrider = CustomAxisValuesOverrider(
                minYValue = minMemoryUsage - 0.02f,
                maxYValue = maxMemoryUsage + 0.02f
            )
        )
    }
}
