package com.example.sampleusbproject.utils

import com.example.sampleusbproject.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class AppUpdateChecker @Inject constructor() {
    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 час
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            REMOTE_APP_VERSION_CODE to BuildConfig.VERSION_CODE.toLong()
        ))
        Timber.d("AppUpdateChecker initialized with version code: ${BuildConfig.VERSION_CODE}")
    }

    suspend fun checkForUpdate(): Boolean {
        return try {
            Timber.d("Checking for updates...")
            remoteConfig.fetchAndActivate().await()
            val remoteVersion = remoteConfig.getLong(REMOTE_APP_VERSION_CODE)
            val hasUpdate = remoteVersion > BuildConfig.VERSION_CODE
            
            Timber.d("""
                Update check result:
                - Current version: ${BuildConfig.VERSION_CODE}
                - Remote version: $remoteVersion
                - Update available: $hasUpdate
            """.trimIndent())
            
            hasUpdate
        } catch (e: Exception) {
            Timber.e(e, "Error checking for updates")
            false
        }
    }

    companion object {
        const val REMOTE_APP_VERSION_CODE = "app_version_code"
        const val REMOTE_APK_PATH = "apk_storage_path"
    }
} 