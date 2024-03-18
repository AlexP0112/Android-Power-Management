package com.example.powermanager.utils

// screen names

// main screens
const val HOME_SCREEN_NAME                          = "Home"
const val LIVE_CHARTS_SCREEN_NAME                   = "Live charts"
const val RECORDING_SCREEN_NAME                     = "Recording"
const val CONTROL_SCREEN_NAME                       = "Control"
const val SETTINGS_SCREEN_NAME                      = "Settings"

// secondary screens
const val RECORDING_RESULT_SCREEN_NAME              = "_recording_result"
const val SCALING_GOVERNORS_EXPLANATION_SCREEN_NAME = "_scaling_governors"
const val CPU_CONFIGURATION_INSPECT_SCREEN_NAME     = "_cpu_configuration_inspect"
const val RECORDING_RESULT_FILE_INSPECT_SCREEN_NAME = "_recording_result_file_inspect"
const val RECORDING_RESULTS_COMPARISON_SCREEN_NAME  = "_recording_results_comparison"

// chart names
const val BATTERY_CHART_NAME        = "Battery percentage in the last %d hours"
const val MEMORY_CHART_NAME         = "Memory usage (GB) in the last %d seconds"
const val CPU_FREQUENCY_CHART_NAME  = "CPU frequency (GHz) in the last %d seconds"
const val CPU_LOAD_CHART_NAME       = "CPU load in the last %d seconds"

// numerical constants
const val NUMBER_OF_BYTES_IN_A_KILOBYTE : Long = 1024L
const val NUMBER_OF_BYTES_IN_A_MEGABYTE : Long = 1024L * 1024L
const val NUMBER_OF_BYTES_IN_A_GIGABYTE : Long = 1024L * 1024 * 1024
const val NUMBER_OF_KILOHERTZ_IN_A_GIGAHERTZ : Int = 1000 * 1000
const val NUMBER_OF_KILOHERTZ_IN_A_MEGAHERTZ : Int = 1000
const val NUMBER_OF_MICROS_IN_A_MILLI : Int = 1000
const val HOURS_IN_A_DAY = 24L
const val MINUTES_IN_AN_HOUR = 60L
const val MILLIS_IN_A_MINUTE = 1000L * 60L
const val MILLIS_IN_A_SECOND = 1000L
const val MILLIS_IN_AN_HOUR = MINUTES_IN_AN_HOUR * MILLIS_IN_A_MINUTE
const val MILLIS_IN_A_DAY = HOURS_IN_A_DAY * MINUTES_IN_AN_HOUR * MILLIS_IN_A_MINUTE

// regex
const val CPU_REGEX = "cpu[0-9]+"
const val ALPHANUMERIC = "[a-zA-Z0-9_-]+"
const val LOAD_AVERAGE_SEMICOLON = "load average:"
const val UP = " up "
const val USERS = "users"
const val COMMA = ","
const val MIN = "min"
const val DAYS = "days"
const val SPACE = " "
const val SEMICOLON = ":"

// charts sampling
const val STATISTICS_BACKGROUND_SAMPLING_THRESHOLD_MILLIS = 60L * 1000L // 1 min
const val BATTERY_LEVEL_NUMBER_OF_SAMPLES = 50

// power and performance recording
val RECORDING_SAMPLING_PERIOD_POSSIBLE_VALUES = listOf(500L, 1000L, 2000L, 5000L)
const val DEFAULT_RECORDING_SAMPLING_PERIOD_MILLIS = 1000L
const val DEFAULT_RECORDING_NUMBER_OF_SAMPLES = 30
const val MINIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED = 10
const val MAXIMUM_NUMBER_OF_RECORDING_SAMPLES_ALLOWED = 250
const val CONFIRM_RECORDING_DELETION_TEXT = "Are you sure you want to delete recording result %s?"
const val RECORDING_RESULTS_DIRECTORY_NAME = "recording_results"
const val DOT_JSON = ".json"
const val DOT_PROVIDER = ".provider"
const val WAKELOCK_TIMEOUT = 30L * 60 * 1000 // half an hour
const val WAKELOCK_TAG = "RecordingWakeLockTag"
const val RECORDING_TAG = "recording"

// notifications
const val NOTIFICATION_CHANNEL_ID = "power_manager"
const val NOTIFICATION_CHANNEL_NAME = "Power Manager"
const val RECORDING_STARTED_NOTIFICATION_ID = 12344
const val RECORDING_FINISHED_NOTIFICATION_ID = 12345
const val RECORDING_STARTED_NOTIFICATION_TITLE = "Recording power and performance..."
const val RECORDING_FINISHED_NOTIFICATION_TITLE = "Power and performance recording ended"
const val RECORDING_FINISHED_NOTIFICATION_TEXT = "Result saved in %s.json"
const val RECORDING_STARTED_BUTTON_TEXT = "Stop recording"

// paths
const val CORE_FREQUENCY_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq"
const val POLICY_CURRENT_FREQUENCY_PATH = "/sys/devices/system/cpu/cpufreq/%s/scaling_cur_freq"
const val POLICY_MAX_FREQUENCY_PATH = "/sys/devices/system/cpu/cpufreq/%s/scaling_max_freq"
const val DEVICES_SYSTEM_CPU_PATH = "/sys/devices/system/cpu/"
const val NETWORK_INTERFACES_STATS_PATH = "/proc/net/dev"
const val CURRENT_SCALING_GOVERNOR_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"
const val AVAILABLE_SCALING_GOVERNORS_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors"
const val CPUINFO_PATH = "/proc/cpuinfo"
const val CPUFREQ_DIRECTORY_PATH = "/sys/devices/system/cpu/cpufreq"

// control
const val SAVED_CPU_CONFIGURATIONS_DIRECTORY_NAME = "cpu_configurations"
const val RELATED_CPUS = "related_cpus"
const val SCALING_AVAILABLE_FREQUENCIES = "scaling_available_frequencies"
const val CONFIRM_CPU_CONFIGURATION_DELETION_TEXT = "Are you sure you want to delete configuration %s?"

// linux commands
const val UPTIME_COMMAND = "uptime"
const val GET_NUMBER_OF_PROCESSES_COMMAND = "su -c ps -A | wc -l"
const val GET_NUMBER_OF_THREADS_COMMAND = "su -c ps -AT | wc -l"
const val GET_ALL_WIFI_INTERFACES_COMMAND = "su -c ifconfig -a | grep wlan"
const val DISABLE_INTERFACE_COMMAND = "su -c ifconfig %s down"
const val ENABLE_INTERFACE_COMMAND = "su -c ifconfig %s up"
const val CAT_FILE_AS_ROOT_COMMAND = "su -c cat %s"
const val CHANGE_SCALING_GOVERNOR_FOR_POLICY_COMMAND = "su -c echo %s > /sys/devices/system/cpu/cpufreq/%s/scaling_governor"
const val CHANGE_CORE_STATE_COMMAND = "su -c echo %d > /sys/devices/system/cpu/cpu%d/online"
const val CHANGE_SCALING_MAX_FREQUENCY_FOR_POLICY_COMMAND = "su -c echo %d > /sys/devices/system/cpu/cpufreq/%s/scaling_max_freq"

// others
const val WLAN = "wlan"
const val RMNET = "rmnet"
const val PROCESSOR = "processor"
const val NO_VALUE_STRING = "-"
const val FAILED_TO_DETERMINE = "Failed to determine"
const val JSON_MIME_TYPE = "application/json"
