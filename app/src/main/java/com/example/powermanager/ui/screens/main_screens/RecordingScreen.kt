package com.example.powermanager.ui.screens.main_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.powermanager.ui.screens.common.ConfirmFileDeletionAlertDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.utils.CONFIRM_RECORDING_DELETION_TEXT
import com.example.powermanager.utils.DEFAULT_RECORDING_NUMBER_OF_SAMPLES
import com.example.powermanager.utils.DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS
import com.example.powermanager.utils.RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES
import com.example.powermanager.utils.isFileNameValid
import com.example.powermanager.utils.isRecordingNumberOfSamplesStringValid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecordingScreen(
    topPadding : Dp,
    model : PowerManagerAppModel,
    openRecordingResultViewScreen : () -> Unit,
    openRecordingResultFileInspectScreen : () -> Unit
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
        val context = LocalContext.current

        val uiState by model.recordingScreensUiState.collectAsState()
        var isConfirmDeletionDialogOpen by rememberSaveable { mutableStateOf(false) }
        var recordingSamplingPeriod by rememberSaveable { mutableLongStateOf(DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS) }
        var recordingNumberOfSamplesString by rememberSaveable { mutableStateOf(DEFAULT_RECORDING_NUMBER_OF_SAMPLES.toString()) }
        var recordingSessionName by rememberSaveable { mutableStateOf("") }
        var includeThreadCountInfo by rememberSaveable { mutableStateOf(true) }

        // ================= screen title =================== //

        RecordingScreenTitle()

        Spacer(modifier = Modifier.height(10.dp))

        // ================= New session section =================== //

        SectionHeader(sectionName = stringResource(R.string.new_session))

        RecordingSamplingPeriodRow(
            onSelectedNewValue = { newValue ->
                recordingSamplingPeriod = newValue
            },
            currentValue = recordingSamplingPeriod
        )

        NumberOfSamplesRow(
            keyboardController = keyboardController,
            currentNumberOfSamplesString = recordingNumberOfSamplesString,
            onValueChanged = { newValue ->
                recordingNumberOfSamplesString = newValue
            },
            textFieldEnabled = !uiState.isRecording
        )

        RecordingSessionNameRow(
            keyboardController = keyboardController,
            currentSessionName = recordingSessionName,
            onValueChanged = { newValue ->
                recordingSessionName = newValue
            },
            textFieldEnabled = !uiState.isRecording
        )

        ThreadCountInfoCheckboxRow(
            currentValue = includeThreadCountInfo,
            onValueChanged = { newValue ->
                includeThreadCountInfo = newValue
            },
            checkBoxEnabled = !uiState.isRecording
        )

        Spacer(modifier = Modifier.height(10.dp))

        StartRecordingButtonAndIndicatorRow(
            onButtonPressed = {
                model.startRecording(
                    samplingPeriod = recordingSamplingPeriod,
                    numberOfSamples = recordingNumberOfSamplesString.toInt(),
                    sessionName = recordingSessionName,
                    includeThreadCountInfo = includeThreadCountInfo
                )

                recordingSessionName = ""
            },
            isRecordingInProgress = uiState.isRecording,
            recordingNumberOfSamplesString = recordingNumberOfSamplesString,
            recordingSessionName = recordingSessionName
        )

        if (uiState.isRecording) {
            Spacer (modifier = Modifier.height(10.dp))

            OutlinedButton(onClick = { model.stopRecording() }) {
                Text(text = stringResource(R.string.stop_recording))
            }
        }

        Spacer (modifier = Modifier.height(10.dp))

        // ================= Recent results section =================== //

        SectionHeader(sectionName = stringResource(R.string.recent_results))

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.recordingResults.isNotEmpty()) {
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

        uiState.recordingResults.forEach { resultName ->
            RecordingSessionResultRow(
                resultName = resultName,
                onDeleteButtonPressed = {
                    model.selectRecordingResult(resultName)
                    isConfirmDeletionDialogOpen = true
                },
                onShareButtonPressed = {
                    model.shareRecordingResult(resultName, context)
                },
                onInspectButtonPressed = {
                    model.selectRecordingResult(resultName)
                    openRecordingResultFileInspectScreen()
                },
                onViewResultsButtonPressed = {
                    model.changeSelectedRecordingResult(resultName)
                    openRecordingResultViewScreen()
                }
            )

            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (isConfirmDeletionDialogOpen) {
            ConfirmFileDeletionAlertDialog(
                onDismiss = { isConfirmDeletionDialogOpen = false },
                onConfirm = {
                    model.onConfirmRecordingDeletionRequest()
                    isConfirmDeletionDialogOpen = false
                },
                text = String.format(CONFIRM_RECORDING_DELETION_TEXT, uiState.currentlySelectedRecordingResult)
            )
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
 * A Row composable that contains a text ("Number of samples (5-200)") and  a text field where the
 * number of samples is set by the user
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NumberOfSamplesRow(
    keyboardController: SoftwareKeyboardController?,
    currentNumberOfSamplesString: String,
    onValueChanged: (String) -> Unit,
    textFieldEnabled: Boolean
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(4.5f),
            text = stringResource(R.string.number_of_samples)
        )

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
            singleLine = true,
            enabled = textFieldEnabled
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
    onValueChanged : (String) -> Unit,
    textFieldEnabled: Boolean
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1.8f),
            text = stringResource(R.string.session_name)
        )

        TextField(
            modifier = Modifier.weight(1f),
            value = currentSessionName,
            isError = !isFileNameValid(currentSessionName),
            onValueChange = onValueChanged,
            colors = TextFieldDefaults.colors(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            singleLine = true,
            enabled = textFieldEnabled
        )
    }
}

/*
 * A Row composable that contains a checkbox that controls whether thread count information
 * is included in the recording session or not
 */
@Composable
fun ThreadCountInfoCheckboxRow(
    currentValue: Boolean,
    onValueChanged : (Boolean) -> Unit,
    checkBoxEnabled: Boolean
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = currentValue,
            onCheckedChange = onValueChanged,
            enabled = checkBoxEnabled,
            colors = CheckboxDefaults.colors()
        )

        Text(
            text = stringResource(R.string.include_thread_count)
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
                    isFileNameValid(recordingSessionName)
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
 * A Row composable that contains a text ("Time between samples (milliseconds)"), a button that
 * opens an info dialog and a dropdown menu where the recording sampling period is set by the user
 */
@Composable
fun RecordingSamplingPeriodRow(
    onSelectedNewValue: (Long) -> Unit,
    currentValue: Long
) {
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier.weight(2f),
            text = stringResource(R.string.sampling_period_millis)
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            // text field with the current value selected
            TextField(
                modifier = Modifier.width(100.dp),
                value = currentValue.toString(),
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors()
            )

            // button that expands/collapses the dropdown menu
            IconButton(
                onClick = { dropdownExpanded = !dropdownExpanded }
            ) {
                Icon(
                    imageVector = if (!dropdownExpanded) Icons.Default.KeyboardArrowDown
                                    else Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }

            // dropdown menu with the available values
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = {
                    dropdownExpanded = false
                }
            ) {
                RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES.map { value ->
                    DropdownMenuItem(
                        text = {
                            Text(text = value.toString())
                        },
                        colors = MenuDefaults.itemColors(),
                        onClick = {
                            onSelectedNewValue(value)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}


/*
 * A Row composable corresponding to a "Recent results" entry. It contains a text (the name of the
 * result), a button for deleting it, a button for sharing it, a button for viewing the raw
 * file content and a button that takes you to the `_recording_result` screen
 */
@Composable
fun RecordingSessionResultRow(
    resultName : String,
    onDeleteButtonPressed : () -> Unit,
    onShareButtonPressed : () -> Unit,
    onInspectButtonPressed : () -> Unit,
    onViewResultsButtonPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier.weight(1.3f),
            text = resultName
        )

        // buttons for delete, inspect and view results
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            // delete button
            IconButton(
                onClick = onDeleteButtonPressed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.garbage_trash),
                    tint = Color.Red,
                    contentDescription = null
                )
            }

            // share result button
            IconButton(
                onClick = onShareButtonPressed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.share_svgrepo_com),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
            }

            // view raw file button
            IconButton(
                onClick = onInspectButtonPressed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.magnifier_svgrepo_com),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // view results button
            IconButton(
                onClick = onViewResultsButtonPressed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.eye_icon),
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = null
                )
            }
        }
    }
}
