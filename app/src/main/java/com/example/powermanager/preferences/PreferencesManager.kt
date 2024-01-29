package com.example.powermanager.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.powermanager.R
import kotlinx.coroutines.flow.first

data class PreferenceProperties(
    val nameStringId : Int,
    val descriptionStringId : Int,
    val possibleValues : List<String>,
    val defaultValue : String
)

// IDs that are used as keys in the Preferences Datastore
val allPreferencesIDs = listOf(
    HOME_SCREEN_SAMPLING_PERIOD_ID,
    LOAD_AVERAGE_TYPE_ID
)

class PreferencesManager (
    appContext: Context
){

    private val preferenceKeyToCurrentValue : MutableMap<String, String> = mutableMapOf()
    private var context: Context
    private val preferenceKeyToProperties : Map<String, PreferenceProperties>
    private val preferenceKeyStringToPrefKey : Map<String, Preferences.Key<String>>

    private val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    init {
        context = appContext

        preferenceKeyStringToPrefKey = mapOf(
            HOME_SCREEN_SAMPLING_PERIOD_ID to PreferencesKeys.homeScreenSamplingRate,
            LOAD_AVERAGE_TYPE_ID to PreferencesKeys.loadAverageType
        )

        preferenceKeyToProperties = mapOf(
            HOME_SCREEN_SAMPLING_PERIOD_ID to PreferenceProperties(
                nameStringId = R.string.home_screen_sampling_period_name,
                descriptionStringId = R.string.home_screen_sampling_period_description,
                possibleValues = HOME_SCREEN_SAMPLING_PERIOD_ALLOWED_VALUES.map { it.toString() },
                defaultValue = HOME_SCREEN_SAMPLING_PERIOD_DEFAULT_VALUE
            ),

            LOAD_AVERAGE_TYPE_ID to PreferenceProperties(
                nameStringId = R.string.load_average_type_name,
                descriptionStringId = R.string.load_average_type_description,
                possibleValues = LoadAverageTypes.values().map { it.toString() },
                defaultValue = LoadAverageTypes.LAST_MINUTE.toString()
            )
        )
    }

    suspend fun initializePreferences() {
        for (preferenceID in allPreferencesIDs) {
            var preferenceValue: String = preferenceKeyToProperties[preferenceID]!!.defaultValue

            // read from the disk and check if any value was saved there,
            // otherwise default value is used
            val savedValue = context.dataStore.data.first()[preferenceKeyStringToPrefKey[preferenceID]!!]
            if (savedValue != null)
                preferenceValue = savedValue

            preferenceKeyToCurrentValue[preferenceID] = preferenceValue
        }
    }

    suspend fun updatePreferenceValue(key : String, newValue : String) {
        // update in memory
        preferenceKeyToCurrentValue[key] = newValue

        // update on disk
        context.dataStore.edit { preferences ->
            preferences[preferenceKeyStringToPrefKey[key]!!] = newValue
        }
    }

    fun getCurrentValueForPreference(key: String) : String {
        return preferenceKeyToCurrentValue[key]!!
    }
}

object PreferencesKeys {
    val homeScreenSamplingRate = stringPreferencesKey(HOME_SCREEN_SAMPLING_PERIOD_ID)
    val loadAverageType = stringPreferencesKey(LOAD_AVERAGE_TYPE_ID)
}
