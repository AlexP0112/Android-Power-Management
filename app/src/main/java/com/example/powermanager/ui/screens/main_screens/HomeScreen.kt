package com.example.powermanager.ui.screens.main_screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.powermanager.R
import com.example.powermanager.ui.model.HomeScreenInfo
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.screens.common.InfoDialog
import com.example.powermanager.ui.screens.common.SectionHeader
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
    onGoToLiveChartsButtonClicked : () -> Unit,
    onGoToControlScreenButtonClicked : () -> Unit
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
        val uiState by model.homeScreenUiState.collectAsState()

        // ==============  battery and uptime section  ===================== //
        SectionHeader(
            sectionName = stringResource(R.string.battery_and_uptime)
        )

        // battery status
        SectionMember(
            leftText = stringResource(R.string.battery_status),
            rightText = homeScreenInfo.value.batteryStatus
        )

        // battery health
        SectionMember(
            leftText = stringResource(R.string.battery_health),
            rightText = homeScreenInfo.value.batteryHealthString
        )

        // battery level
        SectionMember(
            leftText = stringResource(R.string.current_battery_level),
            rightText = "${homeScreenInfo.value.currentBatteryLevel}%"
        )

        // battery charge counter
        SectionMember(
            leftText = stringResource(R.string.current_battery_charge),
            rightText = "${homeScreenInfo.value.batteryChargeCount} mAh"
        )

        // battery voltage
        SectionMember(
            leftText = stringResource(R.string.current_battery_voltage),
            rightText = homeScreenInfo.value.batteryVoltageString
        )

        // battery current
        SectionMember(
            leftText = stringResource(R.string.battery_current),
            rightText = homeScreenInfo.value.batteryCurrentString
        )

        // battery temperature
        SectionMember(
            leftText = stringResource(R.string.battery_temperature),
            rightText = homeScreenInfo.value.batteryTemperatureString
        )

        // battery cycles
        SectionMember(
            leftText = stringResource(R.string.battery_charge_discharge_cycles),
            rightText = homeScreenInfo.value.batteryCyclesString
        )

        // power save mode status
        SectionMember(
            leftText = stringResource(R.string.power_save_mode_status),
            rightText = if (homeScreenInfo.value.powerSaveState)
                stringResource(R.string.enabled) else
                stringResource(R.string.not_enabled)
        )

        // uptime
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
            rightText = "${String.format("%.2f", model.getTotalMemory())} GB"
        )

        // used memory
        SectionMember(
            leftText = stringResource(R.string.used_memory),
            rightText = "${String.format("%.2f", homeScreenInfo.value.usedMemoryGB)} GB (${usedMemoryPercentage}%)"
        )

        // available memory
        SectionMember(
            leftText = stringResource(R.string.available_memory),
            rightText = "${String.format("%.2f", model.getTotalMemory() - homeScreenInfo.value.usedMemoryGB)} GB (${freeMemoryPercentage}%)"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ==============  CPU info section  ===================== //
        SectionHeader(
            sectionName = stringResource(R.string.cpu_information)
        )

        // total number of cores
        SectionMember(
            leftText = stringResource(R.string.total_number_of_cores),
            rightText = model.getTotalNumberOfCores().toString()
        )

        // number of online cores
        SectionMember(
            leftText = stringResource(R.string.number_of_online_cores),
            rightText = homeScreenInfo.value.onlineCores.size.toString()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // cores frequencies
        for (i in 0 until homeScreenInfo.value.onlineCores.size) {
            val coreIndex = homeScreenInfo.value.onlineCores[i]
            val coreFrequency = homeScreenInfo.value.cpuFrequenciesGHz[i]

            SectionMember(
                leftText = "Cpu${coreIndex} frequency",
                rightText = "$coreFrequency GHz"
            )
        }

        // cpu load, with info button that explains its meaning
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(R.string.cpu_load),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                IconButton(onClick = {
                    model.changeHomeScreenInfoDialogState(true)
                }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Text(
                text = homeScreenInfo.value.cpuLoad.toString(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }

        SectionHeader(sectionName = stringResource(R.string.processes_information))

        SectionMember(
            leftText = stringResource(R.string.total_number_of_processes),
            rightText = homeScreenInfo.value.numberOfProcesses.toString()
        )

        SectionMember(
            leftText = stringResource(R.string.total_number_of_threads),
            rightText = homeScreenInfo.value.numberOfThreads.toString()
        )
        
        Spacer(modifier = Modifier.height(10.dp))

        // buttons that take you to live charts screen and control screen
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GoToOtherScreenButton(onClick = onGoToLiveChartsButtonClicked, buttonTextStringID = R.string.go_to_live_charts)
            GoToOtherScreenButton(onClick = onGoToControlScreenButtonClicked, buttonTextStringID = R.string.go_to_control_screen)
        }

        if (uiState.isCPULoadInfoDialogOpen) {
            InfoDialog(
                textId = R.string.cpu_load_explanation,
                cardHeight = 220.dp
            ) {
                model.changeHomeScreenInfoDialogState(false)
            }
        }
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
fun GoToOtherScreenButton(
    onClick: () -> Unit,
    buttonTextStringID : Int
) {
    OutlinedButton(
        onClick = onClick,
    ) {
        Text(
            text = stringResource(buttonTextStringID),
            textAlign = TextAlign.Center
        )
    }
}
