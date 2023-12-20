package com.example.powermanager.data.sampling

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object CPUFrequencyTracker {
    private var values: SnapshotStateList<Float> = mutableStateListOf()

    fun addValue(value: Float) {
        values.add(value)

        // only keep records from the last minute
        if (values.size > NUMBER_OF_VALUES_TRACKED)
            values.removeAt(0)
    }

    fun getValues() : SnapshotStateList<Float> {
        return values
    }

    fun clearValues() {
        values.clear()
    }
}