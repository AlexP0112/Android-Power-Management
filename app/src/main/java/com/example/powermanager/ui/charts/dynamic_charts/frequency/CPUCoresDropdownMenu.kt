package com.example.powermanager.ui.charts.dynamic_charts.frequency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun CPUCoresDropdownMenu(
    model: PowerManagerAppModel
) {
    val maxCoreNumber = model.getTotalNumberOfCores() - 1
    val cpuNames = (0 .. maxCoreNumber).map { "cpu${it}" }

    val uiState by model.liveChartsScreenUiState.collectAsState()
    var isCoreTrackedDropdownExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // text field with the current core tracked
        TextField(
            modifier = Modifier.width(80.dp),
            value = "cpu${uiState.coreTracked}",
            onValueChange = {},
            readOnly = true,
            colors = TextFieldDefaults.colors()
        )

        // button that expands/collapses the dropdown menu
        IconButton(
            onClick = { isCoreTrackedDropdownExpanded = !isCoreTrackedDropdownExpanded }
        ) {
            Icon(
                imageVector = if (!isCoreTrackedDropdownExpanded) Icons.Default.KeyboardArrowDown
                else Icons.Default.KeyboardArrowUp,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = isCoreTrackedDropdownExpanded,
            onDismissRequest = { isCoreTrackedDropdownExpanded = false }
        ) {
            cpuNames.map {
                DropdownMenuItem(
                    text = {
                        Text(text = it)
                    },
                    colors = MenuDefaults.itemColors(),
                    onClick = {
                        model.changeFrequencyChartTrackedCore(it.substring(3).toInt())
                        isCoreTrackedDropdownExpanded = false
                    }
                )
            }
        }
    }
}
