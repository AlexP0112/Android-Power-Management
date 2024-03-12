package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun CpuConfigurationFileInspectScreen(
    topPadding: Dp,
    model: PowerManagerAppModel
) {
    Column(
        Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val fileContent = model.getSelectedConfigurationFileContent()

        Text(
            text = fileContent
        )
    }
}