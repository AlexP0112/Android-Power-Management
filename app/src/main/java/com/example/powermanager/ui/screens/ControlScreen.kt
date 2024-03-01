package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader

@Composable
fun ControlScreen(
    topPadding: Dp,
    goToDisplaySettings: () -> Unit
) {
    Column(
        Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isDozeModeInfoDialogOpen = remember { mutableStateOf(false) }

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

        OutlinedButton(onClick = goToDisplaySettings) {
            Text(stringResource(R.string.go_to_display_settings))
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
        text = stringResource(R.string.power_and_performance_control),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
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
