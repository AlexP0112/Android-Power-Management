package com.example.powermanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.powermanager.R
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.model.HomeScreenInfo
import com.example.powermanager.utils.formatDuration

@Composable
fun HomeScreen(
    topPadding: Dp,
    onBack: () -> Unit,
    model: AppModel
) {
    val homeScreenInfo = model.homeScreenInfoFlow.collectAsStateWithLifecycle(initialValue = HomeScreenInfo())

    BackHandler(enabled = true, onBack = onBack)
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
        // battery info section
        SectionHeader(sectionName = stringResource(R.string.battery_information))

        // charging status
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = stringResource(R.string.charging_status)
            )
            Text(
                text = if (homeScreenInfo.value.isBatteryCharging) stringResource(R.string.charging) else stringResource(
                    R.string.not_charging
                )
            )
        }

        // battery level
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = stringResource(R.string.current_battery_level)
            )
            Text(
                text = "${homeScreenInfo.value.currentBatteryLevel}%"
            )
        }

        // battery charge/discharge prediction
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = if (homeScreenInfo.value.isBatteryCharging)
                        stringResource(R.string.time_until_full_charge)
                        else stringResource(R.string.remaining_battery_life)
            )
            Text(
                text = formatDuration(homeScreenInfo.value.chargeOrDischargePrediction)
            )
        }
    }
}

@Composable
fun SectionHeader(
    sectionName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            modifier = Modifier
                .weight(2f),
            text = sectionName,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}