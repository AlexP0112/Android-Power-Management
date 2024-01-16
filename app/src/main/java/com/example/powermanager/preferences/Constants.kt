package com.example.powermanager.preferences

enum class LoadAverageTypes {
    LAST_MINUTE,
    LAST_FIVE_MINUTES,
    LAST_FIFTEEN_MINUTES
}

const val HOME_SCREEN_SAMPLING_PERIOD_ID = "home_screen_sampling_period"
val HOME_SCREEN_SAMPLING_PERIOD_ALLOWED_VALUES = listOf(500L, 1000L, 2000L, 4000L, 10000L)
const val HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE = 2000L

const val LOAD_AVERAGE_TYPE_ID = "load_average_type"