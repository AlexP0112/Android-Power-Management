package com.example.powermanager.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Date
import java.util.Locale

object FormattingUtils {

    fun getDateTimeNiceString(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun formatDuration(duration: Duration?) : String {
        if (duration == null || duration.seconds <= 0L)
            return NO_VALUE_STRING

        var result = ""

        if (duration.toDays() > 0)
            result += "${duration.toDays()}d "

        if (duration.toHours() % HOURS_IN_A_DAY != 0L)
            result += "${duration.toHours() % HOURS_IN_A_DAY}h "

        if (duration.toMinutes() % MINUTES_IN_AN_HOUR != 0L)
            result += "${duration.toMinutes() % MINUTES_IN_AN_HOUR}min"

        return result
    }

    fun getHourAndMinuteFromLongTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val timeOfDay = date.toString().split(" ")[3]
        val hourAndMinute = timeOfDay.split(":").dropLast(1)
        val hour = hourAndMinute[0]
        val minute = hourAndMinute[1]

        return "$hour:$minute"
    }

    fun getPrettyStringFromNumberOfBytes(byteCount: Long) : String {
        // return as number of bytes
        if (byteCount < NUMBER_OF_BYTES_IN_A_KILOBYTE)
            return "${byteCount}B"

        // return as number of gigabytes
        if (byteCount > NUMBER_OF_BYTES_IN_A_GIGABYTE)
            return String.format("%.1fGB", byteCount.toFloat() / NUMBER_OF_BYTES_IN_A_GIGABYTE.toFloat())

        // return as number of megabytes
        if (byteCount > NUMBER_OF_BYTES_IN_A_MEGABYTE)
            return String.format("%.1fMB", byteCount.toFloat() / NUMBER_OF_BYTES_IN_A_MEGABYTE.toFloat())

        // return as number of kilobytes
        return String.format("%.1fKB", byteCount.toFloat() / NUMBER_OF_BYTES_IN_A_KILOBYTE.toFloat())
    }

}
