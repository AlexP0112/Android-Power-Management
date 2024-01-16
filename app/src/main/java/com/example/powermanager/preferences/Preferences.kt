package com.example.powermanager.preferences

import com.example.powermanager.R

data class AppPreference<E>(
    val id : String,
    val nameStringId : Int,
    val descriptionStringId : Int,
    val allowedValues : List<E>,
    val defaultValue : E
)

val allPreferences = listOf(
    AppPreference(
        id = HOME_SCREEN_SAMPLING_PERIOD_ID,
        nameStringId = R.string.home_screen_sampling_period_name,
        descriptionStringId = R.string.home_screen_sampling_period_description,
        allowedValues = HOME_SCREEN_SAMPLING_PERIOD_ALLOWED_VALUES,
        defaultValue = HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE
    ),

    AppPreference(
        id = LOAD_AVERAGE_TYPE_ID,
        nameStringId = R.string.load_average_type_name,
        descriptionStringId = R.string.load_average_type_description,
        allowedValues = LoadAverageTypes.values().toList(),
        defaultValue = LoadAverageTypes.LAST_MINUTE
    )
)
