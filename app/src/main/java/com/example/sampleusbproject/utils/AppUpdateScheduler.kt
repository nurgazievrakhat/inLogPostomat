package com.example.sampleusbproject.utils

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sampleusbproject.worker.AppUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleUpdateCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val updateWorkRequest = PeriodicWorkRequestBuilder<AppUpdateWorker>(
            6, TimeUnit.HOURS // Проверка каждые 6 часов
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "app_update_check",
            ExistingPeriodicWorkPolicy.KEEP,
            updateWorkRequest
        )
    }
}