package com.example.powermanager.utils

object ConversionUtils {

    fun convertBytesToGigaBytes(bytes: Long) : Float {
        return bytes.toFloat() / (NUMBER_OF_BYTES_IN_A_GIGABYTE.toFloat())
    }

    fun convertKHzToGHz(value: Int) : Float {
        return value.toFloat() / NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ.toFloat()
    }

    fun convertMicroAmpsToMilliAmps(value: Int) : Int {
        return value / NUMBER_OF_MICROS_IN_A_MILLI
    }

}
