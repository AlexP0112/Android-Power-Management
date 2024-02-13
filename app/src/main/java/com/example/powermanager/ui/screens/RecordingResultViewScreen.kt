package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.recording.model.RecordingResult
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.state.AppUiState

@Composable
fun RecordingResultViewScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
) {
    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiState: State<AppUiState> = model.uiState.collectAsState()
        val result: RecordingResult = model.getCurrentlySelectedRecordingResult()

        // title of the screen
        Text(
            text = "Recording session ${uiState.value.currentlySelectedRecordingResult}",
            fontWeight = FontWeight.Bold
        )
    }
}