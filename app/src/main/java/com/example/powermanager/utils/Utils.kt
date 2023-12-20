package com.example.powermanager.utils

import java.io.File
import java.io.FileFilter
import java.util.Date
import java.util.regex.Pattern

const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024 * 1024 * 1024
const val NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ : Int = 1000 * 1000
const val DEVICES_SYSTEM_CPU_PATH = "/sys/devices/system/cpu/"
const val CPU_REGEX = "cpu[0-9]+"

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

fun convertKHzToGHz(value: Int) : Float {
    return value.toFloat() / NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ.toFloat()
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