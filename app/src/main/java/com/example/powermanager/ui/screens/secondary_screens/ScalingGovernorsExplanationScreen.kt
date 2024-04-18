package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.control.cpu.GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID

@Composable
fun ScalingGovernorsExplanationScreen(
    topPadding: Dp,
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
        Text(
            text = stringResource(id = R.string.scaling_governors_explanation),
            fontSize = 17.sp
        )
        
        GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID.forEach { (name, descriptionId) ->
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\u2022 $name",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = descriptionId)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}