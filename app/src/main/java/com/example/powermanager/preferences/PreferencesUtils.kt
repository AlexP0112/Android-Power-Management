package com.example.powermanager.preferences

object PreferenceValueAdaptor {

    /*
     * Function takes a preference ID and the current value of the preference as a string
     * and returns that value converted to the appropriate type for each preference.
     *
     * This is necessary because all preferences are stored as strings
     */
    fun preferenceStringValueToActualValue(
        preferenceID: String,
        preferenceValueAsString: String
    ) : Any {
        return when (preferenceID) {
            LOAD_AVERAGE_TYPE_ID -> {
                when (preferenceValueAsString) {
                    "1 min" -> LoadAverageTypes.LAST_MINUTE
                    "5 min" -> LoadAverageTypes.LAST_FIVE_MINUTES
                    else -> LoadAverageTypes.LAST_FIFTEEN_MINUTES
                }
            }

            RECORDING_FINISHED_NOTIFICATION_ENABLED_ID -> preferenceValueAsString == "Yes"

            NUMBER_OF_RECORDINGS_LISTED_ID -> preferenceValueAsString.toInt()

            else -> preferenceValueAsString.toLong()
        }
    }
}