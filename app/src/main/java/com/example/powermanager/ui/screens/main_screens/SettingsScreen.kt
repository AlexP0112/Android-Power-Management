package com.example.powermanager.ui.screens.main_screens

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
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

            var isDropdownExpanded by remember {
                mutableStateOf(false)
            }

            var currentPreferenceValue by remember {
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
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
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
                
                Spacer(modifier = Modifier.width(10.dp))

                // the dropdown with the possible values, default value is marked with Bold
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { newValue ->
                            isDropdownExpanded = newValue
                        }
                    ) {
                        TextField(
                            value = currentPreferenceValue,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                            },
                            colors = TextFieldDefaults.colors(),
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = {
                                isDropdownExpanded = false
                            }
                        ) {
                            preferenceProperties.possibleValues.map { value ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = value,
                                            fontWeight = if (value == preferenceProperties.defaultValue) FontWeight.ExtraBold else null
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
