package com.example.powermanager.utils

// screen names
const val HOME_SCREEN_NAME          = "Home"
const val LIVE_CHARTS_SCREEN_NAME   = "Live charts"
const val RECORDING_SCREEN_NAME     = "Recording"
const val CONTROL_SCREEN_NAME       = "Control"
const val SETTINGS_SCREEN_NAME      = "Settings"

// chart names
const val BATTERY_CHART_NAME        = "Battery percentage in the last %d hours"
const val MEMORY_CHART_NAME         = "Memory usage (GB) in the last %d seconds"
const val CPU_FREQUENCY_CHART_NAME  = "CPU frequency (GHz) in the last %d seconds"
const val CPU_LOAD_CHART_NAME       = "CPU load in the last %d seconds"

const val NUMBER_OF_BYTES_IN_A_KILOBYTE : Long = 1024L
const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024L * 1024 * 1024
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
const val ALPHANUMERIC = "[a-zA-Z0-9_]+"
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

// charts sampling
const val STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 60L * 1000L // 1 min
const val BATTERY_LEVEL_NUMBER_OF_SAMPLES = 50

// power and performance recording
val RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES = listOf(500L, 1000L, 2000L, 5000L)
const val DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS = 1000L
const val DEFAULT_RECORDING_NUMBER_OF_SAMPLES = 30
const val DEFAULT_RECORDING_NAME = "default"
const val MINIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED = 5
const val MAXIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED = 200
const val CONFIRM_DELETION_TEXT = "Are you sure you want to delete recording result %s?"
const val STORAGE_DIRECTORY_NAME = "recording_results"
const val DOT_JSON = ".json"

// notifications
const val NOTIFICATION_CHANNEL_ID = "power_manager"
const val NOTIFICATION_CHANNEL_NAME = "Power Manager"
const val NOTIFICATION_ID = 12345
const val NOTIFICATION_TITLE = "Power and performance recording ended"
const val NOTIFICATION_TEXT = "Result saved in %s.json"

// paths
const val CORE_FREQUENCY_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq"
const val DEVICES_SYSTEM_CPU_PATH = "/sys/devices/system/cpu/"

// linux commands
const val UPTIME_COMMAND = "uptime"
