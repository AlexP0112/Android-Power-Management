package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.ui.state.AppUiState
import com.example.powermanager.utils.RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES
import com.example.powermanager.utils.isRecordingNumberOfSamplesStringValid
import com.example.powermanager.utils.isRecordingSessionNameValid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecordingScreen(
    topPadding: Dp,
    model : PowerManagerAppModel
) {
    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 8.dp,
                end = 8.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        // ================= screen state variables =================== //

        val uiState: State<AppUiState> = model.uiState.collectAsState()

        var isSamplingPeriodDropdownExpanded by remember {
            mutableStateOf(false)
        }

        var isNumberOfSamplesInfoDialogOpen by remember {
            mutableStateOf(false)
        }

        val sessionResults = remember {
            // TODO change this
            mutableStateListOf("result111", "result222", "result333", "result444", "result555")
        }

        // ================= screen title variables =================== //

        RecordingScreenTitle()

        Spacer(modifier = Modifier.height(10.dp))

        // ================= New session section =================== //

        SectionHeader(sectionName = stringResource(R.string.new_session))

        SamplingPeriodRow(
            onDismissDropdownMenu = {
                isSamplingPeriodDropdownExpanded = false
            },
            onSelectedNewValue = { newValue ->
                model.changeRecordingSamplingPeriod(newValue)
                isSamplingPeriodDropdownExpanded = false
            },
            isSamplingPeriodDropdownExpanded = isSamplingPeriodDropdownExpanded,
            currentValue = uiState.value.recordingSamplingPeriod,
            onChangedExpandedValue = { newValue ->
                isSamplingPeriodDropdownExpanded = newValue
            }
        )

        NumberOfSamplesRow(
            onIconButtonPressed = {
                isNumberOfSamplesInfoDialogOpen = true
            },
            keyboardController = keyboardController,
            currentNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
            onValueChanged = { newValue ->
                model.changeRecordingNumberOfSamplesString(newValue)
            }
        )

        RecordingSessionNameRow(
            keyboardController = keyboardController,
            currentSessionName = uiState.value.recordingSessionName,
            onValueChanged = { input ->
                model.changeRecordingSessionName(input)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        StartRecordingButtonAndIndicatorRow(
            onButtonPressed = {
                model.onStartRecording()
            },
            isRecordingInProgress = uiState.value.isRecording,
            recordingNumberOfSamplesString = uiState.value.recordingNumberOfSamplesString,
            recordingSessionName = uiState.value.recordingSessionName
        )

        Spacer (modifier = Modifier.height(10.dp))

        // ================= Recent results section =================== //

        SectionHeader(sectionName = stringResource(R.string.recent_results))

        Spacer(modifier = Modifier.height(10.dp))

        if (sessionResults.isNotEmpty()) {
            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = stringResource(R.string.no_results_found),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }

        sessionResults.forEach { resultName ->
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    modifier = Modifier.weight(3f),
                    text = resultName
                )

                // icons for delete and view results
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    // delete button
                    IconButton(
                        onClick = {
                            sessionResults -= resultName
                        /*TODO*/
                        }
                    ) {
                       Icon(
                           painter = painterResource(id = R.drawable.garbage_trash),
                           tint = Color.Red,
                           contentDescription = null
                       )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // view results button
                    IconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.eye_icon),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = null
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // ================= Info dialog =================== //

        if (isNumberOfSamplesInfoDialogOpen) {
            InfoDialog(
                textId = R.string.number_of_samples_value_explanation,
                cardHeight = 100.dp
            ) {
                isNumberOfSamplesInfoDialogOpen = false
            }
        }
    }
}

@Composable
fun RecordingScreenTitle() {
    Text(
        text = stringResource(R.string.power_and_performance_recording),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}

/*
 * A Row composable that contains a text ("Number of samples"), a button that opens an info
 * dialog and a text field where the number of samples is set by the user
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NumberOfSamplesRow(
    onIconButtonPressed: () -> Unit,
    keyboardController : SoftwareKeyboardController?,
    currentNumberOfSamplesString: String,
    onValueChanged : (String) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            modifier = Modifier.weight(4.5f),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = stringResource(R.string.number_of_samples)
            )

            IconButton(onClick = onIconButtonPressed) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        TextField(
            modifier = Modifier.weight(1f),
            value = currentNumberOfSamplesString,
            isError = !isRecordingNumberOfSamplesStringValid(currentNumberOfSamplesString),
            onValueChange = onValueChanged,
            colors = TextFieldDefaults.colors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            singleLine = true
        )
    }
}

/*
 * A Row composable that contains a text ("Session name (optional)") and a text field where the
 * name of the new recording session is set by the user
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecordingSessionNameRow(
    keyboardController : SoftwareKeyboardController?,
    currentSessionName: String,
    onValueChanged : (String) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1.5f),
            text = stringResource(R.string.session_name_optional)
        )

        TextField(
            modifier = Modifier.weight(1f),
            value = currentSessionName,
            isError = !isRecordingSessionNameValid(currentSessionName),
            onValueChange = onValueChanged,
            colors = TextFieldDefaults.colors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            singleLine = true
        )
    }
}

/*
 * A Row composable that contains a button for starting the recording session and, only if a
 * session is in progress, a red circle as an indicator
 */
@Composable
fun StartRecordingButtonAndIndicatorRow(
    onButtonPressed: () -> Unit,
    isRecordingInProgress: Boolean,
    recordingNumberOfSamplesString: String,
    recordingSessionName: String
) {
    Row (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedButton(
            onClick = onButtonPressed,
            enabled = !isRecordingInProgress &&
                    isRecordingNumberOfSamplesStringValid(recordingNumberOfSamplesString) &&
                    isRecordingSessionNameValid(recordingSessionName)
        ) {
            Text(
                text = if (isRecordingInProgress) stringResource(R.string.recording)
                else stringResource(R.string.start_recording),
                textAlign = TextAlign.Center
            )
        }

        if (isRecordingInProgress) {
            Spacer(modifier = Modifier.width(15.dp))

            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.recording_filled_svgrepo_com),
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

/*
 * A Row composable that contains a text ("Sampling period (milliseconds)") and a dropdown menu from
 * where the recording sampling period is set by the user
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamplingPeriodRow(
    onDismissDropdownMenu: () -> Unit,
    onSelectedNewValue: (Long) -> Unit,
    isSamplingPeriodDropdownExpanded : Boolean,
    currentValue : Long,
    onChangedExpandedValue: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier.weight(2.5f),
            text = stringResource(R.string.sampling_period_millis)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // the dropdown with the possible values
        Box(
            modifier = Modifier.weight(1f)
        ) {
            ExposedDropdownMenuBox(
                expanded = isSamplingPeriodDropdownExpanded,
                onExpandedChange = onChangedExpandedValue
            ) {
                TextField(
                    value = currentValue.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSamplingPeriodDropdownExpanded)
                    },
                    colors = TextFieldDefaults.colors(),
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isSamplingPeriodDropdownExpanded,
                    onDismissRequest = onDismissDropdownMenu
                ) {
                    RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES.map { value ->
                        DropdownMenuItem(
                            text = {
                                Text(text = value.toString())
                            },
                            colors = MenuDefaults.itemColors(),
                            onClick = { onSelectedNewValue(value) }
                        )
                    }
                }
            }
        }
    }
}
