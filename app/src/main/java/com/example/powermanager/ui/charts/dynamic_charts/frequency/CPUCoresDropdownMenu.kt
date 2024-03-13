package com.example.powermanager.ui.charts.dynamic_charts.frequency

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import com.example.powermanager.ui.model.PowerManagerAppModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CPUCoresDropdownMenu(
    model: PowerManagerAppModel
) {
    val maxCoreNumber = model.getTotalNumberOfCores() - 1
    val cpuNames = (0 .. maxCoreNumber).map { "cpu${it}" }

    val uiState by model.liveChartsScreenUiState.collectAsState()
    var isCoreTrackedDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isCoreTrackedDropdownExpanded,
        onExpandedChange = { isCoreTrackedDropdownExpanded = it }
    ) {
        TextField(
            value = "cpu${uiState.coreTracked}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isCoreTrackedDropdownExpanded
                )
            },
            colors = TextFieldDefaults.colors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
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
