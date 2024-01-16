package com.example.powermanager.preferences

import com.example.powermanager.R

data class PreferenceProperties(
    val nameStringId : Int,
    val descriptionStringId : Int,
    val allowedValues : List<String>,
    val defaultValue : String
)

val allPermissionIDs = listOf(
    HOME_SCREEN_SAMPLING_PERIOD_ID,
    LOAD_AVERAGE_TYPE_ID
)

object PreferencesManager {

    private val preferenceKeyToCurrentValue : MutableMap<String, String> = mutableMapOf()

    private val preferenceKeyToProperties : Map<String, PreferenceProperties> = mapOf(
        HOME_SCREEN_SAMPLING_PERIOD_ID to PreferenceProperties(
            nameStringId = R.string.home_screen_sampling_period_name,
            descriptionStringId = R.string.home_screen_sampling_period_description,
            allowedValues = HOME_SCREEN_SAMPLING_PERIOD_ALLOWED_VALUES.map { it.toString() },
            defaultValue = HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE.toString()
        ),

        LOAD_AVERAGE_TYPE_ID to PreferenceProperties(
            nameStringId = R.string.load_average_type_name,
            descriptionStringId = R.string.load_average_type_description,
            allowedValues = LoadAverageTypes.values().map { it.toString() },
            defaultValue = LoadAverageTypes.LAST_MINUTE.toString()
        )
    )

    suspend fun initializePreferences() {
        for (permissionID in allPermissionIDs)
            preferenceKeyToCurrentValue[permissionID] = getInitialValueForPreference(permissionID)
    }

    suspend fun updatePreferenceValue(key : String, newValue : String) {

    }

    fun getCurrentValueForPreference(key: String) : String {
        return preferenceKeyToCurrentValue[key]!!
    }

    private suspend fun getInitialValueForPreference(key : String) : String {
        return ""
    }

}
