package com.example.powermanager.recording.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object Recorder {

    suspend fun record(
        samplingPeriod: Long,
        numberOfSamples: Int,
        sessionName: String,
        onRecordingFinished: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            // TODO initial setup

            for (i in 0 until numberOfSamples) {
                // TODO actual sampling

                delay(samplingPeriod)
            }

            // TODO final stuff
            onRecordingFinished()
        }
    }
}