package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.DEFAULT_RECORDING_NAME
import com.example.powermanager.utils.DEFAULT_RECORDING_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS

@Composable
fun RecordingScreen(
    topPadding: Dp,
    model : PowerManagerAppModel
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
        val state: State<AppUiState> = model.uiState.collectAsState()

        var isSamplingPeriodDropdownExpanded by remember {
            mutableStateOf(false)
        }

        var samplingPeriod by remember {
            mutableLongStateOf(DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS)
        }

        var numberOfSamples by remember {
            mutableIntStateOf(DEFAULT_RECORDING_NUMBER_OF_SAMPLES)
        }

        var recordingName by remember {
            mutableStateOf(DEFAULT_RECORDING_NAME)
        }

        // title of the screen
        Text(
            text = stringResource(R.string.power_and_performance_recording),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // new session section

        SectionHeader(sectionName = stringResource(R.string.new_session))

        Icon(
            painter = painterResource(id = R.drawable.recording_filled_svgrepo_com),
            contentDescription = null,
            tint = Color.Red
        )

        // dropdown to select the sampling period, similar to preferences screen

        // text field for number of samples

        // recording name

        // "Start recording" button, along with indicator

        // results section

        SectionHeader(sectionName = stringResource(R.string.recent_results))
    }
}