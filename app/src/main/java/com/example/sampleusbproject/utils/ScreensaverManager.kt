package com.example.sampleusbproject.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.sampleusbproject.R

class ScreensaverManager(
    private val activity: AppCompatActivity,
    private val idleTimeMillis: Long = 300000
) {
    private var userActivityHandler = Handler(Looper.getMainLooper())
    private var isScreensaverShown = false
    private val navController by lazy { activity.findNavController(R.id.nav_host) }

    private val showScreensaverRunnable = Runnable {
        if (!isScreensaverShown) {
            navController.navigate(R.id.action_to_screensaver)
            isScreensaverShown = true
        }
    }

    // Детектор активности пользователя
    private val userActivityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            if (activity == this@ScreensaverManager.activity) {
                resetIdleTimer()
            }
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityDestroyed(activity: Activity) {}
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    // Детектор касаний экрана
    private val touchListener = View.OnTouchListener { _, _ ->
        resetIdleTimer()
        false
    }

    fun start() {
        // Регистрируем слушатель жизненного цикла активности
        activity.application.registerActivityLifecycleCallbacks(userActivityCallback)

        // Устанавливаем слушатель касаний на корневой View активности
        activity.window.decorView.setOnTouchListener(touchListener)

        // Запускаем таймер
        resetIdleTimer()
    }

    fun stop() {
        // Отменяем таймер
        userActivityHandler.removeCallbacks(showScreensaverRunnable)

        // Удаляем слушатели
        activity.application.unregisterActivityLifecycleCallbacks(userActivityCallback)
        activity.window.decorView.setOnTouchListener(null)
    }

    fun resetIdleTimer() {
        userActivityHandler.removeCallbacks(showScreensaverRunnable)
        isScreensaverShown = false
        userActivityHandler.postDelayed(showScreensaverRunnable, idleTimeMillis)
    }
}