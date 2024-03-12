package com.example.powermanager.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import com.example.powermanager.R

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

@Composable
fun InfoDialog(
    textId : Int,
    cardHeight : Dp,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(
                text = stringResource(textId),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .wrapContentSize(Alignment.Center),
            )
        }
    }
}

/*
 * Alert dialog that asks for user confirmation when deleting a file, with two buttons
 * for confirming and canceling
 */
@Composable
fun ConfirmFileDeletionAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    text: String
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = text,
                fontSize = 17.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
