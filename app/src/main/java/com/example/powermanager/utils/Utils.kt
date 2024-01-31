package com.example.powermanager.utils

import com.example.powermanager.preferences.LoadAverageTypes
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.InputStreamReader
import java.time.Duration
import java.util.Calendar
import java.util.Date
import java.util.regex.Pattern

fun getHourAndMinuteFromLongTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val timeOfDay = date.toString().split(" ")[3]
    val hourAndMinute = timeOfDay.split(":").dropLast(1)
    val hour = hourAndMinute[0]
    val minute = hourAndMinute[1]

    return "$hour:$minute"
}

fun convertBytesToGigaBytes(bytes: Long) : Float {
    return bytes.toFloat() / (NUMBER_OF_BYTES_IN_A_GIGABYTE.toFloat())
}

fun convertKHzToGHz(value: Int) : Float {
    return value.toFloat() / NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ.toFloat()
}

fun convertMicroAmpsToMilliAmps(value: Int) : Int {
    return value / NUMBER_OF_MICROS_IN_A_MILLI
}

fun determineNumberOfCPUCores(): Int {

    class CpuFilter : FileFilter {
        override fun accept(pathname: File): Boolean {
            return Pattern.matches(CPU_REGEX, pathname.name)
        }
    }

    return try {
        val dir = File(DEVICES_SYSTEM_CPU_PATH)
        val files = dir.listFiles(CpuFilter()) ?: return 1
        files.size
    } catch (e: Exception) {
        1
    }
}

fun determineSystemBootTimestamp() : Long {
    // execute the uptime command
    val process = Runtime.getRuntime().exec(UPTIME_COMMAND)
    val uptimeOutput = BufferedReader(InputStreamReader(process.inputStream)).readText()
    process.waitFor()

    var systemUptimeMillis = 0L

    // get the section of the output that represents the actual uptime
    val uptimeParts = uptimeOutput.split(UP)[1] // output after "up"
                        .split(USERS)[0] // output before "users"
                        .split(COMMA).dropLast(1) // exclude the number of users
                        .map { it.trim() } // eliminate the white spaces in the parts

    // parse this section
    for (part in uptimeParts) {
        if (part.contains(MIN)) // X min
            systemUptimeMillis += part.split(SPACE)[0].trim().toLong() * MILLIS_IN_A_MINUTE

        if (part.contains(DAYS)) // X days
            systemUptimeMillis += part.split(SPACE)[0].trim().toLong() * MILLIS_IN_A_DAY

        if (part.contains(SEMICOLON)) // X:Y, where X = hours, Y = minutes
            systemUptimeMillis += part.split(SEMICOLON)[0].trim().toLong() * MILLIS_IN_AN_HOUR +
                    part.split(SEMICOLON)[1].trim().toLong() * MILLIS_IN_A_MINUTE
    }

    return Calendar.getInstance().timeInMillis - systemUptimeMillis
}

fun getLoadAverageFromUptimeCommandOutput(commandOutput: String, loadAverageType: LoadAverageTypes): Float {
    val allLoads = commandOutput.split(LOAD_AVERAGE_SEMICOLON)[1].trim()
    val load = allLoads.split(COMMA)[loadAverageType.ordinal]

    return load.toFloat()
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

fun isRecordingNumberOfSamplesStringValid(numberOfSamplesString : String) : Boolean {
    return numberOfSamplesString.toIntOrNull() != null &&
            numberOfSamplesString.toInt() in MINIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED..MAXIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED
}

fun isRecordingSessionNameValid(name : String) : Boolean {
    return name.matches(ALPHANUMERIC.toRegex())
}