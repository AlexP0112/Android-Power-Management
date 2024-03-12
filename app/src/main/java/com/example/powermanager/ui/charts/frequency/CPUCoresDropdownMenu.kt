package com.example.powermanager.ui.charts.frequency

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.state.LiveChartsScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CPUCoresDropdownMenu(
    model: PowerManagerAppModel
) {
    val maxCoreNumber = model.getTotalNumberOfCores() - 1
    val cpuNames = (0 .. maxCoreNumber).map { "cpu${it}" }

    val uiState: State<LiveChartsScreenUiState> = model.liveChartsScreenUiState.collectAsState()

    ExposedDropdownMenuBox(
        expanded = uiState.value.isCoreTrackedDropdownExpanded,
        onExpandedChange = { model.changeTrackedCoreDropdownMenuState(it) }
    ) {
        TextField(
            value = "cpu${uiState.value.coreTracked}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = uiState.value.isCoreTrackedDropdownExpanded
                )
            },
            colors = TextFieldDefaults.colors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = uiState.value.isCoreTrackedDropdownExpanded,
            onDismissRequest = { model.changeTrackedCoreDropdownMenuState(false) }
        ) {
            cpuNames.map {
                DropdownMenuItem(
                    text = {
                        Text(text = it)
                    },
                    colors = MenuDefaults.itemColors(),
                    onClick = {
                        model.changeFrequencyChartTrackedCore(it.substring(3).toInt())
                    }
                )
            }
        }
    }
}
