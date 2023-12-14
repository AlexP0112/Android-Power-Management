package com.example.powermanager.ui.model

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.powermanager.utils.getGigaBytesFromBytes
import java.util.concurrent.TimeUnit

const val NUMBER_OF_VALUES_TRACKED = 60

class MemoryWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE)
        val info = ActivityManager.MemoryInfo()

        (am as ActivityManager).getMemoryInfo(info)
        val usedMemory = info.totalMem - info.availMem

        // add the value that was recorder in the tracker
        MemoryLoadTracker.addValue(getGigaBytesFromBytes(usedMemory))

        // schedule another activity
        val constraints = Constraints.Builder().build()
        val workRequest = OneTimeWorkRequestBuilder<MemoryWorker>()
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setInputData(Data.EMPTY)
            .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).beginUniqueWork("Memory sampling work", ExistingWorkPolicy.REPLACE, workRequest).enqueue()

        return Result.success()
    }
}

object MemoryLoadTracker {
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
}