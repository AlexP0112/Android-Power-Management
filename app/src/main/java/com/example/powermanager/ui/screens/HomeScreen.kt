package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.powermanager.R
import com.example.powermanager.ui.model.HomeScreenInfo
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME
import com.example.powermanager.utils.formatDuration
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
    navController: NavController
) {
    val homeScreenInfo = model.homeScreenInfoFlow.collectAsStateWithLifecycle(initialValue = HomeScreenInfo())
    val usedMemoryPercentage = ((homeScreenInfo.value.usedMemoryGB / model.getTotalMemory()) * 100f).roundToInt()
    val freeMemoryPercentage = 100 - usedMemoryPercentage

    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ==============  battery and uptime section  ===================== //
        SectionHeader(
            sectionName = stringResource(R.string.battery_and_uptime)
        )

        // charging status
        SectionMember(
            leftText = stringResource(R.string.charging_status),
            rightText = if (homeScreenInfo.value.isBatteryCharging)
                    stringResource(R.string.charging) else
                    stringResource(R.string.not_charging)
        )

        // battery level
        SectionMember(
            leftText = stringResource(R.string.current_battery_level),
            rightText = "${homeScreenInfo.value.currentBatteryLevel}%"
        )

        // battery charge/discharge prediction
        SectionMember(
            leftText = if (homeScreenInfo.value.isBatteryCharging)
                    stringResource(R.string.time_until_full_charge) else
                    stringResource(R.string.remaining_battery_life),
            rightText = formatDuration(homeScreenInfo.value.chargeOrDischargePrediction)
        )

        // power save mode status
        SectionMember(
            leftText = stringResource(R.string.power_save_mode_status),
            rightText = if (homeScreenInfo.value.powerSaveState)
                stringResource(R.string.enabled) else
                stringResource(R.string.not_enabled)
        )

        SectionMember(
            leftText = stringResource(R.string.system_uptime),
            rightText = homeScreenInfo.value.systemUptimeString
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ==============  memory info section  ===================== //
        SectionHeader(
            sectionName = stringResource(R.string.memory_information)
        )

        // total memory
        SectionMember(
            leftText = stringResource(R.string.total_memory),
            rightText = "${String.format("%.2f", model.getTotalMemory())}GB"
        )

        // used memory
        SectionMember(
            leftText = stringResource(R.string.used_memory),
            rightText = "${String.format("%.2f", homeScreenInfo.value.usedMemoryGB)}GB (${usedMemoryPercentage}%)"
        )

        // available memory
        SectionMember(
            leftText = stringResource(R.string.available_memory),
            rightText = "${String.format("%.2f", model.getTotalMemory() - homeScreenInfo.value.usedMemoryGB)}GB (${freeMemoryPercentage}%)"
        )
        
        Spacer(modifier = Modifier.height(10.dp))

        // ==============  CPU info section  ===================== //
        SectionHeader(
            sectionName = stringResource(R.string.cpu_information)
        )

        // number of cores
        SectionMember(
            leftText = stringResource(R.string.number_of_cores),
            rightText = model.getNumCores().toString()
        )

        // cores frequencies
        homeScreenInfo.value.cpuFrequenciesGHz.forEachIndexed { index, frequency ->
            SectionMember(
                leftText = "Cpu${index} frequency",
                rightText = "${frequency}GHz"
            )
        }

        // cpu load
        SectionMember(
            leftText = stringResource(R.string.cpu_load),
            rightText = homeScreenInfo.value.cpuLoad.toString()
        )

        GoToChartButton(
            onClick = {
                navController.navigate(STATISTICS_SCREEN_NAME)
            }
        )
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
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            modifier = Modifier
                .weight(3f),
            text = sectionName,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SectionMember(
    leftText : String,
    rightText : String
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = leftText,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Text(
            text = rightText,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}

@Composable
fun GoToChartButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
    ) {
        Text(
            text = stringResource(R.string.go_to_charts),
            textAlign = TextAlign.Center
        )
    }
}