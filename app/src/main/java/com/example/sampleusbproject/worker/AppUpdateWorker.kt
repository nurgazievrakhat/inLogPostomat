package com.example.sampleusbproject.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sampleusbproject.BuildConfig
import com.example.sampleusbproject.R
import com.example.sampleusbproject.utils.AppUpdateChecker
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AppUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var appUpdateChecker: AppUpdateChecker

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val storage = Firebase.storage
    private val remoteConfig = Firebase.remoteConfig

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Проверяем наличие обновления
            if (!appUpdateChecker.checkForUpdate()) {
                return@withContext Result.success()
            }

            // Создаем канал уведомлений
            createNotificationChannel()

            // Показываем уведомление о начале загрузки
            showDownloadingNotification()

            // Получаем путь к APK из Remote Config
            val apkPath = remoteConfig.getString(AppUpdateChecker.REMOTE_APK_PATH)
            val storageRef = storage.reference.child(apkPath)

            // Создаем временный файл для загрузки APK
            val localFile = File(context.cacheDir, "update.apk")
            
            // Загружаем APK
            storageRef.getFile(localFile).await()

            // Создаем URI для установки APK
            val apkUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                localFile
            )

            // Запускаем установку
            installApk(apkUri)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDownloadingNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_cloud_download_24)
            .setContentTitle("Загрузка обновления")
            .setProgress(0, 0, true)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun installApk(apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    companion object {
        private const val CHANNEL_ID = "app_update_channel"
        private const val NOTIFICATION_ID = 1
    }
} 