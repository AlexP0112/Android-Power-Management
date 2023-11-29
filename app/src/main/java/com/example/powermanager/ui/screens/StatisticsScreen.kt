package com.example.powermanager.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.powermanager.ui.model.BatteryLevelTracker

@Composable
fun StatisticsScreen(
    topPadding: Dp
) {
    Column (
        modifier = Modifier.padding(topPadding)
    ) {
        BatteryLevelTracker.getRecords().forEach { record ->
            Text(modifier = Modifier.align(Alignment.CenterHorizontally),
                text = record.level.toString() + "% at " + record.hour.toString() + ":" + record.minute.toString())
        }
    }
}