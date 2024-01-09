package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.R
import com.example.powermanager.ui.charts.battery.BatteryLevelChart
import com.example.powermanager.ui.charts.frequency.CPUCoresDropdownMenu
import com.example.powermanager.ui.charts.frequency.CPUFrequencyChart
import com.example.powermanager.ui.charts.load.CPULoadChart
import com.example.powermanager.ui.charts.memory.MemoryChart
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun StatisticsScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
) {
    val batteryLevelChartRefresher: MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // battery
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // battery percentage chart title
            Text(
                text = stringResource(R.string.battery_level_chart_title),
                fontWeight = FontWeight.Bold
            )
            // refresh button for battery percentage screen
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { batteryLevelChartRefresher.value = !batteryLevelChartRefresher.value }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        BatteryLevelChart(refreshChart = batteryLevelChartRefresher)

        // memory
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.memory_chart_title),
                fontWeight = FontWeight.Bold
            )
        }
        MemoryChart(
            totalMemoryGB = model.getTotalMemory(),
            model = model
        )

        // cpu frequency
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {

            Box(
                modifier = Modifier
                    .weight(2.5f)
                    .padding(horizontal = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.cpu_frequency_chart_title),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                CPUCoresDropdownMenu(model)
            }
        }
        CPUFrequencyChart(
            model = model
        )

        // cpu load
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.cpu_load_chart_title),
                fontWeight = FontWeight.Bold
            )
        }
        CPULoadChart(
            model = model
        )
    }
}


