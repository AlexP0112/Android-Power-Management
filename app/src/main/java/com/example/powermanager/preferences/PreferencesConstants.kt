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
val HOME_SCREEN_SAMPLING_PERIOD_POSSIBLE_VALUES = listOf("500", "1000", "2000", "4000")
const val HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE = "2000"

const val LIVE_CHARTS_SAMPLING_PERIOD_ID = "live_charts_sampling_period"
val LIVE_CHARTS_SAMPLING_PERIOD_POSSIBLE_VALUES = listOf("500", "1000", "2000", "4000")
const val LIVE_CHARTS_SAMPLING_PERIOD_DEFAULT_VALUE = "1000"

const val LIVE_CHARTS_TRACKED_PERIOD_ID = "live_charts_tracked_period"
val LIVE_CHARTS_TRACKED_PERIOD_POSSIBLE_VALUES = listOf("30", "60", "90", "120")
const val LIVE_CHARTS_TRACKED_PERIOD_DEFAULT_VALUE = "60"

const val BATTERY_CHART_TRACKED_PERIOD_ID = "battery_chart_tracked_period"
val BATTERY_CHART_TRACKED_PERIOD_POSSIBLE_VALUES = listOf("6", "12", "24", "48")
const val BATTER_CHART_TRACKED_PERIOD_DEFAULT_VALUE = "24"

const val LOAD_AVERAGE_TYPE_ID = "load_average_type"
val LOAD_AVERAGE_TYPE_POSSIBLE_VALUES = listOf("1 min", "5 min", "15 min")
const val LOAD_AVERAGE_TYPE_DEFAULT_VALUE = "1 min"

const val RECORDING_FINISHED_NOTIFICATION_ENABLED_ID = "recording_finished_notification_enabled"
val RECORDING_FINISHED_NOTIFICATION_ENABLED_POSSIBLE_VALUES = listOf("Yes", "No")
const val RECORDING_FINISHED_NOTIFICATION_ENABLED_DEFAULT_VALUE = "Yes"

const val NUMBER_OF_RECORDINGS_LISTED_ID = "number_of_recordings_listed"
val NUMBER_OF_RECORDINGS_LISTED_POSSIBLE_VALUES = listOf("5", "10", "15", "25")
const val NUMBER_OF_RECORDINGS_LISTED_DEFAULT_VALUE = "10"

// IDs that are used as keys in the Preferences Datastore
val allPreferencesIDs = listOf(
    HOME_SCREEN_SAMPLING_PERIOD_ID,
    LIVE_CHARTS_SAMPLING_PERIOD_ID,
    LIVE_CHARTS_TRACKED_PERIOD_ID,
    BATTERY_CHART_TRACKED_PERIOD_ID,
    LOAD_AVERAGE_TYPE_ID,
    NUMBER_OF_RECORDINGS_LISTED_ID,
    RECORDING_FINISHED_NOTIFICATION_ENABLED_ID
)

object PreferencesKeys {
    val homeScreenSamplingPeriod = stringPreferencesKey(HOME_SCREEN_SAMPLING_PERIOD_ID)
    val liveChartsSamplingPeriod = stringPreferencesKey(LIVE_CHARTS_SAMPLING_PERIOD_ID)
    val liveChartsTrackedPeriod = stringPreferencesKey(LIVE_CHARTS_TRACKED_PERIOD_ID)
    val batteryChartTrackedPeriod = stringPreferencesKey(BATTERY_CHART_TRACKED_PERIOD_ID)
    val loadAverageType = stringPreferencesKey(LOAD_AVERAGE_TYPE_ID)
    val numberOfRecordingsListed = stringPreferencesKey(NUMBER_OF_RECORDINGS_LISTED_ID)
    val recordingFinishedNotificationEnabled = stringPreferencesKey(RECORDING_FINISHED_NOTIFICATION_ENABLED_ID)
}