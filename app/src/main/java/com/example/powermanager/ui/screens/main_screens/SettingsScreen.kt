package com.example.powermanager.ui.screens.main_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.powermanager.R
import com.example.powermanager.preferences.allPreferencesIDs
import com.example.powermanager.ui.model.PowerManagerAppModel

@Composable
fun SettingsScreen(
    topPadding: Dp,
    model : PowerManagerAppModel
) {
    Column(
        modifier = Modifier
            .padding(
                top = topPadding + 5.dp,
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // title of the screen
        Text(
            text = stringResource(R.string.application_settings),
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // the actual preferences

        allPreferencesIDs.forEach { preferenceID ->
            val preferenceProperties = model.getPreferenceProperties(preferenceID)

            var isDropdownExpanded by rememberSaveable {
                mutableStateOf(false)
            }

            var currentPreferenceValue by rememberSaveable {
                mutableStateOf(model.getPreferenceValue(preferenceID))
            }

            Divider(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    // the name of the preference
                    Text(
                        text = stringResource(id = preferenceProperties.nameStringId),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // the description of the preference
                    Text(
                        text = stringResource(id = preferenceProperties.descriptionStringId)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(modifier = Modifier.width(10.dp))

                    // the current value of the preference
                    TextField(
                        modifier = Modifier.width(100.dp),
                        value = currentPreferenceValue,
                        onValueChange = {},
                        readOnly = true,
                        colors = TextFieldDefaults.colors()
                    )

                    // button that expands/collapses the dropdown menu
                    IconButton(
                        onClick = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        Icon(
                            imageVector = if (!isDropdownExpanded) Icons.Default.KeyboardArrowDown
                                            else Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    }

                    // dropdown menu with options for the preference value
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        preferenceProperties.possibleValues.forEach { value ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = value,
                                        fontWeight = if (value == preferenceProperties.defaultValue)
                                            FontWeight.ExtraBold else null
                                    )
                                },
                                colors = MenuDefaults.itemColors(),
                                onClick = {
                                    currentPreferenceValue = value
                                    isDropdownExpanded = false
                                    model.onPreferenceValueChanged(preferenceID, value)
                                }
                            )
                        }
                    }
                }
            }
        }

        // for styling purposes
        Divider(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 100.dp, end = 100.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        
        Spacer(modifier = Modifier.height(15.dp))
    }
}
