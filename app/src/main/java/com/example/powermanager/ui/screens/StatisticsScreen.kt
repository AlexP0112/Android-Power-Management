package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.Dp
import com.example.powermanager.R
import com.example.powermanager.ui.charts.BatteryLevelChart
import com.example.powermanager.ui.charts.MemoryChart

@Composable
fun StatisticsScreen(
    topPadding: Dp
) {
    val batteryLevelChartRefresher: MutableState<Boolean> = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = topPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // battery
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // battery percentage chart title
            Text(
                text = stringResource(R.string.battery_level_chart_title)
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
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.memory_chart_title)
            )
        }
        MemoryChart()
    }
}

