package com.example.powermanager.utils

// screen names
const val HOME_SCREEN_NAME          = "Home"
const val LIVE_CHARTS_SCREEN_NAME   = "Live charts"
const val CONTROL_SCREEN_NAME       = "Control"
const val SETTINGS_SCREEN_NAME      = "Settings"

// chart names
const val BATTERY_CHART_NAME        = "Battery percentage in the last %d hours"
const val MEMORY_CHART_NAME         = "Memory usage (GB) in the last %d seconds"
const val CPU_FREQUENCY_CHART_NAME  = "CPU frequency (GHz) in the last %d seconds"
const val CPU_LOAD_CHART_NAME       = "CPU load in the last %d seconds"

const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024 * 1024 * 1024
const val NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ : Int = 1000 * 1000
const val NUMBER_OF_MICROS_IN_A_MILLI : Int = 1000
const val HOURS_IN_A_DAY = 24L
const val MINUTES_IN_AN_HOUR = 60L
const val MILLIS_IN_A_MINUTE = 1000L * 60L
const val MILLIS_IN_A_SECOND = 1000L
const val MILLIS_IN_AN_HOUR = MINUTES_IN_AN_HOUR * MILLIS_IN_A_MINUTE
const val MILLIS_IN_A_DAY = HOURS_IN_A_DAY * MINUTES_IN_AN_HOUR * MILLIS_IN_A_MINUTE

// regex
const val CPU_REGEX = "cpu[0-9]+"
const val LOAD_AVERAGE_SEMICOLON = "load average:"
const val UP = " up "
const val USERS = "users"
const val COMMA = ","
const val MIN = "min"
const val DAYS = "days"
const val SPACE = " "
const val SEMICOLON = ":"

const val NO_VALUE_STRING = "-"
const val FAILED_TO_DETERMINE = "Failed to determine"

// sampling
const val STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 60L * 1000L // 1 min
const val BATTERY_LEVEL_NUMBER_OF_SAMPLES = 50

// paths
const val CORE_FREQUENCY_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq"
const val DEVICES_SYSTEM_CPU_PATH = "/sys/devices/system/cpu/"

// linux commands
const val UPTIME_COMMAND = "uptime"
