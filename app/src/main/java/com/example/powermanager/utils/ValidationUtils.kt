package com.example.powermanager.utils

object ValidationUtils {

    fun isRecordingNumberOfSamplesStringValid(numberOfSamplesString : String) : Boolean {
        return numberOfSamplesString.toIntOrNull() != null &&
                numberOfSamplesString.toInt() in MINIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED..MAXIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED
    }

    fun isFileNameValid(name : String) : Boolean {
        return name.matches(ALPHANUMERIC.toRegex())
    }

}
