package com.example.sampleusbproject

import android.app.Application
import com.example.sampleusbproject.utils.AppUpdateScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var appUpdateScheduler: AppUpdateScheduler

    override fun onCreate() {
        super.onCreate()
        appUpdateScheduler.scheduleUpdateCheck()
    }
}