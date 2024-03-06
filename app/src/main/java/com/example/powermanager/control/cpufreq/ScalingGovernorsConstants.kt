package com.example.powermanager.control.cpufreq

import com.example.powermanager.R

const val GOVERNOR_PERFORMANCE = "performance"
const val GOVERNOR_POWERSAVE = "powersave"
const val GOVERNOR_ONDEMAND = "ondemand"
const val GOVERNOR_USERSPACE = "userspace"
const val GOVERNOR_CONSERVATIVE = "conservative"
const val GOVERNOR_SCHEDUTIL = "schedutil"

const val DEFAULT_GOVERNOR_STRING = "(default)"

val GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID : Map<String, Int> = mapOf(
    GOVERNOR_PERFORMANCE to R.string.performance_governor_description,
    GOVERNOR_POWERSAVE to R.string.powersave_governor_description,
    GOVERNOR_ONDEMAND to R.string.ondemand_governor_description,
    GOVERNOR_USERSPACE to R.string.userspace_governor_description,
    GOVERNOR_CONSERVATIVE to R.string.conservative_governor_description,
    GOVERNOR_SCHEDUTIL to R.string.schedutil_governor_description
)
