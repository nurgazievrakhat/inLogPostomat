package com.example.sampleusbproject.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sampleusbproject.BuildConfig
import com.example.sampleusbproject.R
import com.example.sampleusbproject.admin.KioskManager
import com.example.sampleusbproject.utils.AppUpdateChecker
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@HiltWorker
class AppUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val appUpdateChecker: AppUpdateChecker,
    private val kioskManager: KioskManager
) : CoroutineWorker(context, params) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val remoteConfig = Firebase.remoteConfig

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Timber.d("Starting update check worker")

            // Проверяем наличие обновления
            if (!appUpdateChecker.checkForUpdate()) {
                Timber.d("No update available")
                return@withContext Result.success()
            }

            Timber.d("Update available, starting download process")
            createNotificationChannel()
            showDownloadingNotification()

            // Получаем ссылку на APK из Remote Config
            remoteConfig.fetchAndActivate().await()
            val apkUrl = remoteConfig.getString(AppUpdateChecker.REMOTE_APK_PATH)
            Timber.d("APK url from remote config: $apkUrl")
            if (apkUrl.isNullOrBlank()) throw Exception("APK url is empty in Remote Config")

            val localFile = File(context.cacheDir, "update.apk")
            Timber.d("Local file path: ${localFile.absolutePath}")

            // Скачиваем APK через OkHttp
            val client = OkHttpClient()
            val request = Request.Builder().url(apkUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Failed to download APK: ${response.code}")
                response.body?.byteStream()?.use { input ->
                    FileOutputStream(localFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
            Timber.d("APK download completed")

            // Создаем URI для установки APK
            val apkUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                localFile
            )
            Timber.d("APK URI created: $apkUri")

            // Запускаем установку
            if (kioskManager.isDeviceOwner()) installApk(apkUri)
            else installSilently(apkUri)
            Timber.d("Installation started")

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during update process")
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
    fun installSilently(apkUri: Uri) {
        val packageInstaller = context.packageManager.packageInstaller

        val inputStream = context.contentResolver.openInputStream(apkUri)
            ?: throw IllegalArgumentException("Unable to open APK input stream from URI")
        val availableBytes = inputStream.available().toLong()

        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        val out = session.openWrite("app_install", 0, availableBytes)
        inputStream.copyTo(out)
        session.fsync(out)
        out.close()
        inputStream.close()

        val intent = Intent("com.example.INSTALL_COMPLETE")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        session.commit(pendingIntent.intentSender)
        session.close()
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        restartIntent?.let {
            context.startActivity(it)
        }
    }
    companion object {
        private const val CHANNEL_ID = "app_update_channel"
        private const val NOTIFICATION_ID = 1
    }

}