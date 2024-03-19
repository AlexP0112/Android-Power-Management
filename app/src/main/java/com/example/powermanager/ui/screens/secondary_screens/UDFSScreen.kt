package com.example.powermanager.ui.screens.secondary_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.utils.UDFS_NOTIFICATION_CONTINUE_BUTTON_TEXT
import com.example.powermanager.utils.UDFS_NOTIFICATION_STOP_BUTTON_TEXT

@Composable
fun UDFSScreen(
    topPadding: Dp,
    model: PowerManagerAppModel,
    openControlScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
                start = 6.dp,
                end = 6.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        UDFSScreenTitle()

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.udfs_definition)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.udfs_explanation1)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "\t\u2022 $UDFS_NOTIFICATION_CONTINUE_BUTTON_TEXT",
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "\t\u2022 $UDFS_NOTIFICATION_STOP_BUTTON_TEXT",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.udfs_explanation2)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.udfs_note)
        )

        OutlinedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                model.startUDFSProcess()
                openControlScreen()
            }
        ) {
            Text(text = stringResource(R.string.start))
        }
    }
}

@Composable
fun UDFSScreenTitle() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.udfs),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}
