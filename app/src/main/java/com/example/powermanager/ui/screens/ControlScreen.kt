package com.example.powermanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ControlScreen(
    topPadding: Dp,
) {
    Box (
    ) {
        Text(modifier = Modifier.padding(top = topPadding),
            text = "Control screen")
    }
}

