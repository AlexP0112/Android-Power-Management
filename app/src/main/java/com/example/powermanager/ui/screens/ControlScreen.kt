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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import com.example.powermanager.ui.state.AppUiState

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
        val uiState: State<AppUiState> = model.uiState.collectAsState()
        val availableScalingGovernors : List<String> = model.getAvailableScalingGovernors()

        ControlScreenTitle()

        Spacer(modifier = Modifier.height(10.dp))

        // ================= Wi-Fi section ==================== //
        SectionHeader(sectionName = stringResource(R.string.wi_fi))

        WifiText()

        DozeModeInfoRow {
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

        SelectScalingGovernorText()

        availableScalingGovernors.forEach { scalingGovernor ->
            ScalingGovernorRow(
                governorName = scalingGovernor,
                isSelected = scalingGovernor == uiState.value.currentScalingGovernor
            ) {
                model.changeScalingGovernor(scalingGovernor)
            }
        }

        // ================= info dialogs ==================== //
        if (isDozeModeInfoDialogOpen.value) {
            InfoDialog(
                textId = R.string.doze_mode_explanation,
                cardHeight = 250.dp
            ) {
                isDozeModeInfoDialogOpen.value = false
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
fun SelectScalingGovernorText() {
    Text(
        text = stringResource(R.string.select_scaling_governor),
        fontSize = 18.sp
    )
}

@Composable
fun DarkThemeText() {
    Text(
        text = stringResource(R.string.dark_theme_text),
        fontSize = 18.sp
    )
}

@Composable
fun DozeModeInfoRow(
    onIconButtonPressed : () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.doze_mode_explanation_intro))

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

/*
 * A Row that corresponds to a scaling governor. It contains a radio button, the name of the
 * governor
 */
@Composable
fun ScalingGovernorRow(
    governorName : String,
    isSelected : Boolean,
    onSelected : () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
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
