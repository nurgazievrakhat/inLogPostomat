package com.example.sampleusbproject.utils

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sampleusbproject.worker.AppUpdateWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleUpdateCheck() {
        Timber.d("Scheduling update check")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val updateWorkRequest = PeriodicWorkRequestBuilder<AppUpdateWorker>(
            1, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "app_update_check",
            ExistingPeriodicWorkPolicy.KEEP,
            updateWorkRequest
        )
        
        Timber.d("Update check scheduled every 6 hours")
    }
}