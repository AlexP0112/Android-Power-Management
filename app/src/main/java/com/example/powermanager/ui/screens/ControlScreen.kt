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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.control.cpufreq.DEFAULT_GOVERNOR_STRING
import com.example.powermanager.control.cpufreq.GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.ConfirmFileDeletionAlertDialog
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.InspectFileInfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.ui.state.ControlScreenUiState
import com.example.powermanager.utils.CONFIRM_CPU_CONFIGURATION_DELETION_TEXT
import com.example.powermanager.utils.isFileNameValid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ControlScreen(
    topPadding: Dp,
    goToDisplaySettings: () -> Unit,
    model: PowerManagerAppModel
) {
    Column(
        Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val uiState: State<ControlScreenUiState> = model.controlScreenUiState.collectAsState()
        val keyboardController = LocalSoftwareKeyboardController.current

        val availableScalingGovernors : List<String> = model.getAvailableScalingGovernors()
        val totalNumberOfCores = model.getTotalNumberOfCores()
        val cpuFreqPolicies = model.getCpuFreqPolicies()
        val masterCores = model.getMasterCores()

        ControlScreenTitle()

        Spacer(modifier = Modifier.height(10.dp))

        // ================= Wi-Fi section ==================== //
        SectionHeader(sectionName = stringResource(R.string.wi_fi))

        WifiText()

        TextAndInfoButtonRow(
            textId = R.string.doze_mode_explanation_intro,
            fontSize = 16
        ) {
            model.changeControlScreenInfoDialogParams(
                textId = R.string.doze_mode_explanation,
                heightDp = 250.dp
            )
        }

        // ================= display section ==================== //
        SectionHeader(sectionName = stringResource(R.string.display))

        ScreenBrightnessText()
        ScreenTimeoutText()
        DarkThemeText()

        // go to display settings button
        GoToDisplaySettingsButton(goToDisplaySettings)

        // ================= CPU section ==================== //
        SectionHeader(sectionName = stringResource(id = R.string.cpu))

        TextAndInfoButtonRow(
            textId = R.string.select_scaling_governor,
            fontSize = 18
        ) {
            model.changeControlScreenInfoDialogParams(
                textId = R.string.scaling_governors_explanation,
                heightDp = 180.dp
            )
        }

        availableScalingGovernors.forEach { scalingGovernor ->
            ScalingGovernorRow(
                governorName = scalingGovernor,
                isSelected = scalingGovernor == uiState.value.currentScalingGovernor,
                onSelected = {
                    model.changeScalingGovernor(scalingGovernor)
                },
                onIconButtonPressed = {
                    model.changeControlScreenInfoDialogParams(
                        textId = GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID[scalingGovernor]!!,
                        heightDp = 240.dp
                    )
                }
            )
        }

        TextAndInfoButtonRow(
            textId = R.string.online_cpu_cores,
            fontSize = 18
        ) {
            model.changeControlScreenInfoDialogParams(
                textId = R.string.core_enabling_explanation,
                heightDp = 160.dp
            )
        }

        ( 0 until totalNumberOfCores).forEach { coreIndex ->
            CpuCoreOnlineRow(
                canCoreBeDisabled = !masterCores.contains(coreIndex),
                coreNumber = coreIndex,
                onValueChanged = { model.changeCoreEnabledState(coreIndex, it) },
                isCoreOnline = !uiState.value.disabledCores.contains(coreIndex)
            )
        }
        
        Text(
            text = stringResource(id = R.string.set_max_frequency),
            fontSize = 18.sp
        )

        // dropdown for each group of cores that belong to a policy
        cpuFreqPolicies.forEach { policy ->
            val onlineCores = policy.relatedCores.filter { !uiState.value.disabledCores.contains(it) }

            var coresText  = ""
            onlineCores.forEach { coresText += "Cpu$it/" }
            coresText = coresText.dropLast(1)

            var isDropdownExpanded by remember {
                mutableStateOf(false)
            }

            Spacer(modifier = Modifier.height(10.dp))

            SelectMaxFrequencyRow(
                coresText = coresText,
                isDropdownExpanded = isDropdownExpanded,
                onExpendedChange = { isDropdownExpanded = it },
                maxFrequency = policy.maximumFrequencyGhz,
                onDismiss = { isDropdownExpanded = false },
                value = uiState.value.policyToFrequencyLimitMHz[policy.name].toString(),
                availableFrequencies = policy.frequenciesMhz,
                onSelectedEntry = {
                    isDropdownExpanded = false
                    model.changeMaxFrequencyForPolicy(
                        policyName = policy.name,
                        maxFrequencyMhz = it
                    )
                }
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        // save current cpu configuration
        SaveCpuConfigurationText()

        Spacer(modifier = Modifier.height(10.dp))

        SaveCpuConfigurationRow(
            currentCpuConfigurationName = uiState.value.cpuConfigurationName,
            onValueChanged = { model.changeCpuConfigurationName(it) },
            keyboardController = keyboardController,
            onSaveButtonPressed = { model.saveCurrentCpuConfiguration() },
            buttonEnabled = isFileNameValid(uiState.value.cpuConfigurationName)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // previously saved configurations
        SavedConfigurationsText()

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.value.savedConfigurations.isNotEmpty()) {
            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.no_configurations_found),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }

        uiState.value.savedConfigurations.forEach { configurationName ->
            CpuConfigurationRow(
                configurationName = configurationName,
                onDeleteButtonPressed = {
                    model.onCpuConfigurationDeleteButtonPressed(configurationName)
                },
                onInspectButtonPressed = {
                    model.onCpuConfigurationInspectButtonPressed(configurationName)
                },
                onApplyButtonPressed = {
                    model.applySelectedCpuConfiguration(configurationName)
                }
            )

            Divider(
                modifier = Modifier
                    .fillMaxSize(),
                thickness = 0.75.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // ================= info dialogs ==================== //
        if (uiState.value.infoDialogTextId != null) {
            InfoDialog(
                textId = uiState.value.infoDialogTextId!!,
                cardHeight = uiState.value.infoDialogHeightDp
            ) {
                model.changeControlScreenInfoDialogParams(null)
            }
        }

        if (uiState.value.isInspectConfigurationDialogOpen) {
            InspectFileInfoDialog(
                content = model.getSelectedConfigurationFileContent(),
                onDismissRequest = { model.closeCpuConfigurationInspectDialog() }
            )
        }

        if (uiState.value.isConfirmConfigurationDeletionDialogOpen) {
            ConfirmFileDeletionAlertDialog(
                onDismiss = { model.onDismissCpuConfigurationDeletionRequest() },
                onConfirm = { model.onConfirmCpuConfigurationDeletionRequest() },
                text = String.format(CONFIRM_CPU_CONFIGURATION_DELETION_TEXT, uiState.value.currentlySelectedCpuConfiguration)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMaxFrequencyRow(
    coresText : String,
    isDropdownExpanded : Boolean,
    onExpendedChange : (Boolean) -> Unit,
    maxFrequency : Float,
    onDismiss : () -> Unit,
    value : String,
    onSelectedEntry : (Int) -> Unit,
    availableFrequencies : List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .weight(2.65f)
                .fillMaxWidth()
        ) {
            Text(
                text = coresText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Maximum frequency: $maxFrequency GHz",
                fontSize = 15.sp
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = onExpendedChange
            ) {
                TextField(
                    value = value,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                    },
                    colors = TextFieldDefaults.colors(),
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = onDismiss
                ) {
                    availableFrequencies.map { value ->
                        DropdownMenuItem(
                            text = {
                                Text(text = value.toString())
                            },
                            colors = MenuDefaults.itemColors(),
                            onClick = { onSelectedEntry(value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlScreenTitle() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.power_and_performance_control),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun WifiText() {
    Text(
        text = stringResource(R.string.wifi_text),
        fontSize = 18.sp
    )
}

@Composable
fun SavedConfigurationsText() {
    Text(
        text = stringResource(R.string.saved_conf),
        fontSize = 18.sp
    )
}


@Composable
fun ScreenTimeoutText() {
    Text(
        text = stringResource(R.string.screen_timeout_text),
        fontSize = 18.sp
    )
}

@Composable
fun ScreenBrightnessText() {
    Text(
        text = stringResource(R.string.screen_brightness_text),
        fontSize = 18.sp
    )
}

@Composable
fun TextAndInfoButtonRow(
    textId : Int,
    fontSize : Int,
    onIconButtonPressed : () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(textId),
            fontSize = fontSize.sp
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
}

@Composable
fun DarkThemeText() {
    Text(
        text = stringResource(R.string.dark_theme_text),
        fontSize = 18.sp
    )
}

@Composable
fun SaveCpuConfigurationText() {
    Text(
        text = stringResource(R.string.save_cpu_configuration),
        fontSize = 18.sp
    )
}

/*
 * A Row that contains a text field where the user writes the name of the configuration and a
 * button to save the current configuration
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SaveCpuConfigurationRow(
    currentCpuConfigurationName : String,
    onValueChanged : (String) -> Unit,
    keyboardController : SoftwareKeyboardController?,
    onSaveButtonPressed: () -> Unit,
    buttonEnabled : Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.weight(2.65f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.configuration_name),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                modifier = Modifier.width(120.dp),
                value = currentCpuConfigurationName,
                isError = !isFileNameValid(currentCpuConfigurationName),
                onValueChange = onValueChanged,
                colors = TextFieldDefaults.colors(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                singleLine = true
            )
        }

        OutlinedButton(
            onClick = onSaveButtonPressed,
            modifier = Modifier.weight(1f),
            enabled = buttonEnabled
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

/*
 * A Row that corresponds to a scaling governor. It contains a radio button, the name of the
 * governor and an info button that opens a dialog with more information about that governor
 *
 * Note that sched_pixel governor does not have an associated info button, since it is not one
 * of the well-known scaling governors and little to none information about it is available
 */
@Composable
fun ScalingGovernorRow(
    governorName : String,
    isSelected : Boolean,
    onSelected : () -> Unit,
    onIconButtonPressed: () -> Unit
) {
    if (GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID.containsKey(governorName)) {
        // known governor (provide description for it)
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelected,
                    colors = RadioButtonDefaults.colors()
                )

                Text(
                    text = governorName
                )
            }

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
    } else {
        // unknown governor, most probably the OEM-specific default
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelected,
                colors = RadioButtonDefaults.colors()
            )

            Text(
                text = "$governorName $DEFAULT_GOVERNOR_STRING"
            )
        }
    }
}

/*
 * Row that corresponds to each cpu core in the system. For some of them (at most half of the
 * total number of cores) there is a clickable checkbox that can disable/enable them
 */
@Composable
fun CpuCoreOnlineRow(
    canCoreBeDisabled : Boolean,
    isCoreOnline : Boolean,
    onValueChanged: (Boolean) -> Unit,
    coreNumber : Int
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCoreOnline,
            onCheckedChange = onValueChanged,
            enabled = canCoreBeDisabled,
            colors = CheckboxDefaults.colors()
        )

        Text(text = "Cpu$coreNumber")
    }
}

@Composable
fun GoToDisplaySettingsButton(
    goToDisplaySettings : () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = goToDisplaySettings
        ) {
            Text(stringResource(R.string.go_to_display_settings))
        }
    }
}

/*
 * A Row composable corresponding to a saved CPU configuration. It contains the name of the
 * configuration, a button that allows you to inspect it and a button for deleting it (similar
 * to recording results) and a button for applying it
 */
@Composable
fun CpuConfigurationRow(
    configurationName: String,
    onDeleteButtonPressed : () -> Unit,
    onInspectButtonPressed : () -> Unit,
    onApplyButtonPressed : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            modifier = Modifier.weight(1f),
            text = configurationName
        )

        // buttons for delete, inspect and apply
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
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

            // apply button
            OutlinedButton(
                onClick = onApplyButtonPressed,
                modifier = Modifier
                    .height(36.dp)
                    .width(100.dp)
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    fontSize = 12.sp
                )
            }
        }
    }
}
