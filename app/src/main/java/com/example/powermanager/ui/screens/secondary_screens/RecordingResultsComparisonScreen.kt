package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.recording.storage.RecordingResult
import com.example.powermanager.ui.model.PowerManagerAppModel

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

        // title of the screen
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${uiState.currentlySelectedRecordingResult} vs ${uiState.selectedToCompareRecordingResult}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}