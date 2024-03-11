package com.example.powermanager.utils

import com.example.powermanager.preferences.LoadAverageTypes
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

fun determineSystemBootTimestamp() : Long {
    try {
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
    } catch (e: Exception) {
        return Calendar.getInstance().timeInMillis
    }
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

fun isFileNameValid(name : String) : Boolean {
    return name.matches(ALPHANUMERIC.toRegex())
}

fun computeListAverage(list : List<Float>) : Float {
    var sum = 0f

    list.forEach {
        sum += it
    }

    return sum / list.size
}

fun getListMaximum(list : List<Float>) : Float {
    if (list.isEmpty())
        return 0f

    var max = list[0]

    list.forEach {
        if (it > max)
            max = it
    }

    return max
}

fun getListMinimum(list : List<Float>) : Float {
    if (list.isEmpty())
        return 0f

    var min = list[0]

    list.forEach {
        if (it < min)
            min = it
    }

    return min
}

fun getDateTimeNiceString(): String {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

fun readProtectedFileContent(filePath : String) : String {
    val command = String.format(CAT_FILE_AS_ROOT_COMMAND, filePath)
    val process = Runtime.getRuntime().exec(command)
    val fileContent = BufferedReader(InputStreamReader(process.inputStream)).readText()
    process.waitFor()

    return fileContent
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

fun getInterfacesFromIfConfigOutput(output: String) : List<String> {
    return output.split("\n")
        .filter { it.trim().isNotEmpty() }
        .map { line ->
        line.split(" ", "\t")[0]
    }
}

fun getBytesSentAndReceivedByAllInterfacesFromFileContent(fileContent: String) : List<Long> {
    var totalBytesReceived = 0L
    var totalBytesSent = 0L

    try {
        fileContent
            .split("\n")
            .drop(2)
            .map { it.trim() }
            .filter { interfaceInfo ->
                interfaceInfo.startsWith(WLAN) || interfaceInfo.startsWith(RMNET)
            }
            .forEach { interfaceInfo ->
                val parts = interfaceInfo.split("\\s+".toRegex())
                totalBytesReceived += parts[1].toLong()
                totalBytesSent += parts[9].toLong()
            }
    } catch (_ : Exception) {

    }

    return listOf(totalBytesReceived, totalBytesSent)
}

fun getOnlineCoresFromFileContent(fileContent: String) : List<Int> {
    val result : MutableList<Int> = mutableListOf()

    fileContent
        .trim()
        .split("\n")
        .filter { line ->
            line.startsWith(PROCESSOR)
        }
        .forEach { processorLine ->
            // this is a line that looks like this: "processor       : <index>"
            val parts = processorLine.split("\\s+".toRegex())
            result.add(parts[2].toInt())
        }

    return result
}
