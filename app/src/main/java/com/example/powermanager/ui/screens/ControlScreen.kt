package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun ControlScreen(
    topPadding: Dp,
    model : PowerManagerAppModel
) {
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
        ControlScreenTitle()


    }
}

@Composable
fun ControlScreenTitle() {
    Text(
        text = stringResource(R.string.power_and_performance_control),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}