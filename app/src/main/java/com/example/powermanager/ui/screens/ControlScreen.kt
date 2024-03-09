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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.control.cpufreq.DEFAULT_GOVERNOR_STRING
import com.example.powermanager.control.cpufreq.GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.ui.state.AppUiState

@OptIn(ExperimentalMaterial3Api::class)
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
        val isDozeModeInfoDialogOpen = remember { mutableStateOf(false) }
        val isScalingGovernorsGeneralInfoDialogOpen = remember { mutableStateOf(false) }
        val isParticularScalingGovernorInfoDialogOpen = remember { mutableStateOf(false) }
        val isCoreEnablingInfoDialogOpen = remember { mutableStateOf(false) }

        val uiState: State<AppUiState> = model.uiState.collectAsState()
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
            isDozeModeInfoDialogOpen.value = true
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
            isScalingGovernorsGeneralInfoDialogOpen.value = true
        }

        availableScalingGovernors.forEach { scalingGovernor ->
            ScalingGovernorRow(
                governorName = scalingGovernor,
                isSelected = scalingGovernor == uiState.value.currentScalingGovernor,
                onSelected = {
                    model.changeScalingGovernor(scalingGovernor)
                },
                onIconButtonPressed = {
                    model.changeSelectedScalingGovernorInfoButton(scalingGovernor)
                    isParticularScalingGovernorInfoDialogOpen.value = true
                }
            )
        }

        TextAndInfoButtonRow(
            textId = R.string.online_cpu_cores,
            fontSize = 18
        ) {
            isCoreEnablingInfoDialogOpen.value = true
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
            val maxFrequency = policy.maximumFrequencyGhz
            val availableFrequencies = policy.frequenciesMhz

            var coresText  = ""
            onlineCores.forEach { coresText += "Cpu$it/" }
            coresText = coresText.dropLast(1)

            var isDropdownExpanded by remember {
                mutableStateOf(false)
            }

            var currentValue by remember {
                mutableIntStateOf(model.getCurrentMaxFrequencyForPolicyMhz(policy.name))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
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
                        onExpandedChange = { newValue ->
                            isDropdownExpanded = newValue
                        }
                    ) {
                        TextField(
                            value = currentValue.toString(),
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
                            onDismissRequest = {
                                isDropdownExpanded = false
                            }
                        ) {
                            availableFrequencies.map { value ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = value.toString())
                                    },
                                    colors = MenuDefaults.itemColors(),
                                    onClick = {
                                        currentValue = value
                                        isDropdownExpanded = false
                                        model.changeMaxFrequencyForPolicy(
                                            policyName = policy.name,
                                            maxFrequencyMhz = value
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }

        Spacer (modifier = Modifier.height(15.dp))

        // ================= info dialogs ==================== //
        if (isDozeModeInfoDialogOpen.value) {
            InfoDialog(
                textId = R.string.doze_mode_explanation,
                cardHeight = 250.dp
            ) {
                isDozeModeInfoDialogOpen.value = false
            }
        }

        if (isScalingGovernorsGeneralInfoDialogOpen.value) {
            InfoDialog(
                textId = R.string.scaling_governors_explanation,
                cardHeight = 180.dp
            ) {
                isScalingGovernorsGeneralInfoDialogOpen.value = false
            }
        }

        if (isParticularScalingGovernorInfoDialogOpen.value &&
            GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID.containsKey(uiState.value.selectedScalingGovernorInfoButton)) {
            InfoDialog(
                textId = GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID[uiState.value.selectedScalingGovernorInfoButton]!!,
                cardHeight = 240.dp
            ) {
                isParticularScalingGovernorInfoDialogOpen.value = false
            }
        }

        if (isCoreEnablingInfoDialogOpen.value) {
            InfoDialog(
                textId = R.string.core_enabling_explanation,
                cardHeight = 160.dp
            ) {
                isCoreEnablingInfoDialogOpen.value = false
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
