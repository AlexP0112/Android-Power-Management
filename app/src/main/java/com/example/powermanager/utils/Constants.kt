package com.example.powermanager.utils

// screen names
const val HOME_SCREEN_NAME = "Home";
const val STATISTICS_SCREEN_NAME = "Statistics";
const val CONTROL_SCREEN_NAME = "Control"

const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024 * 1024 * 1024
const val NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ : Int = 1000 * 1000

const val DEVICES_SYSTEM_CPU_PATH = "/sys/devices/system/cpu/"
const val CPU_REGEX = "cpu[0-9]+"
const val LOAD_AVERAGE_SEMICOLON = "load average:"

// sampling
const val STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 100L * 1000L // 1min 40s
const val STATISTICS_SCREEN_SAMPLING_RATE_MILLIS = 1000L
const val HOME_SCREEN_SAMPLING_RATE_MILLIS = 2000L
const val NUMBER_OF_MILLIS_IN_A_DAY = 1000L * 60 * 60 * 24
const val BATTERY_LEVEL_NUMBER_OF_SAMPLES = 30
const val NUMBER_OF_VALUES_TRACKED = 60

// useful paths for sampling
const val CORE_FREQUENCY_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq"

// linux commands
const val UPTIME_COMMAND = "uptime"
