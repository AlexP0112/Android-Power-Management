package com.example.powermanager.preferences

import androidx.datastore.preferences.core.stringPreferencesKey

enum class LoadAverageTypes {
    LAST_MINUTE,
    LAST_FIVE_MINUTES,
    LAST_FIFTEEN_MINUTES
}

const val USER_PREFERENCES_NAME = "user_preferences"

// preferences IDs, possible values and default values
const val HOME_SCREEN_SAMPLING_PERIOD_ID = "home_screen_sampling_period"
val HOME_SCREEN_SAMPLING_PERIOD_POSSIBLE_VALUES = listOf("500", "1000", "2000", "4000", "10000")
const val HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE = "2000"

const val LOAD_AVERAGE_TYPE_ID = "load_average_type"
val LOAD_AVERAGE_TYPE_POSSIBLE_VALUES = listOf("1 minute", "5 minutes", "15 minutes")
const val LOAD_AVERAGE_TYPE_DEFAULT_VALUE = "1 minute"

// IDs that are used as keys in the Preferences Datastore
val allPreferencesIDs = listOf(
    HOME_SCREEN_SAMPLING_PERIOD_ID,
    LOAD_AVERAGE_TYPE_ID
)

object PreferencesKeys {
    val homeScreenSamplingRate = stringPreferencesKey(HOME_SCREEN_SAMPLING_PERIOD_ID)
    val loadAverageType = stringPreferencesKey(LOAD_AVERAGE_TYPE_ID)
}