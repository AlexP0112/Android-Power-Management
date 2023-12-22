package com.example.powermanager.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HomeScreen(
    topPadding: Dp,
    onBack: () -> Unit
) {
    BackHandler(enabled = true, onBack = onBack)
    Box (
    ) {
        Text(modifier = Modifier.padding(top = topPadding),
            text = "Home screen")
    }
}