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

    var isExpanded by remember {
        mutableStateOf(false)
    }

    var text by remember {
        mutableStateOf("cpu${model.uiState.value.coreTracked}")
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        }
    ) {
        TextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = TextFieldDefaults.colors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            cpuNames.map {
                DropdownMenuItem(
                    text = {
                        Text(text = it)
                    },
                    colors = MenuDefaults.itemColors(),
                    onClick = {
                        if (text != it)
                            model.changeTrackedCore(it.substring(3).toInt())
                        text = it
                        isExpanded = false
                    }
                )
            }
        }
    }
}
