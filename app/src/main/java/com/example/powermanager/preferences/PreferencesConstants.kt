package com.example.powermanager.preferences

enum class LoadAverageTypes {
    LAST_MINUTE,
    LAST_FIVE_MINUTES,
    LAST_FIFTEEN_MINUTES
}

const val USER_PREFERENCES_NAME = "user_preferences"

const val HOME_SCREEN_SAMPLING_PERIOD_ID = "home_screen_sampling_period"
val HOME_SCREEN_SAMPLING_PERIOD_ALLOWED_VALUES = listOf("500", "1000", "2000", "4000", "10000")
const val HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE = "2000"

const val LOAD_AVERAGE_TYPE_ID = "load_average_type"