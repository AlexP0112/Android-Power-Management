package com.example.powermanager.ui.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(
    sectionName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Divider(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            modifier = Modifier
                .weight(3f),
            text = sectionName,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}