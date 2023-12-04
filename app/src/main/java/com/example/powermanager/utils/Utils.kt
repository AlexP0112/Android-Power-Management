package com.example.powermanager.utils

import java.util.Date

fun getHourAndMinuteFromLongTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val timeOfDay = date.toString().split(" ")[3]
    val hourAndMinute = timeOfDay.split(":").dropLast(1)
    val hour = hourAndMinute[0]
    val minute = hourAndMinute[1]

    return "$hour:$minute"
}