package com.example.powermanager.utils

import java.util.Date

const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024 * 1024 * 1024

fun getHourAndMinuteFromLongTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val timeOfDay = date.toString().split(" ")[3]
    val hourAndMinute = timeOfDay.split(":").dropLast(1)
    val hour = hourAndMinute[0]
    val minute = hourAndMinute[1]

    return "$hour:$minute"
}

fun getGigaBytesFromBytes(bytes: Long) : Float {
    return bytes.toFloat() / (NUMBER_OF_BYTES_IN_A_GIGABYTE.toFloat())
}