package com.example.powermanager.control.cpu

import com.example.powermanager.R

const val GOVERNOR_PERFORMANCE = "performance"
const val GOVERNOR_POWERSAVE = "powersave"
const val GOVERNOR_ONDEMAND = "ondemand"
const val GOVERNOR_USERSPACE = "userspace"
const val GOVERNOR_CONSERVATIVE = "conservative"
const val GOVERNOR_INTERACTIVE = "interactive"
const val GOVERNOR_SCHEDUTIL = "schedutil"

const val DEFAULT_GOVERNOR_STRING = "(default)"

// map from governor name to the ID of its description string
val GOVERNOR_NAME_TO_DESCRIPTION_STRING_ID : Map<String, Int> = mapOf(
    GOVERNOR_PERFORMANCE to R.string.performance_governor_description,
    GOVERNOR_POWERSAVE to R.string.powersave_governor_description,
    GOVERNOR_ONDEMAND to R.string.ondemand_governor_description,
    GOVERNOR_USERSPACE to R.string.userspace_governor_description,
    GOVERNOR_CONSERVATIVE to R.string.conservative_governor_description,
    GOVERNOR_SCHEDUTIL to R.string.schedutil_governor_description,
    GOVERNOR_INTERACTIVE to R.string.interactive_governor_description
)

// map from a number that represents the number of possible frequencies for a policy to a list
// of indices of the chosen values. Each list contains 9 values (9 steps as used in UDFS)
val NUMBER_OF_AVAILABLE_FREQUENCIES_TO_CHOSEN_INDICES : Map<Int, List<Int>> = mapOf(
    8 to listOf(0, 1, 2, 3, 4, 5, 6, 6, 7),
    9 to listOf(0, 1, 2, 3, 4, 5, 6, 7, 8),
    10 to listOf(0, 1, 2, 3, 4, 6, 7, 8, 9),
    11 to listOf(0, 1, 2, 3, 5, 6, 8, 9, 10),
    14 to listOf(0, 1, 2, 3, 5, 7, 9, 11, 13),
    17 to listOf(0, 1, 3, 5, 7, 9, 11, 13, 16)
)
