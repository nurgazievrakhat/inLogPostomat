package com.example.sampleusbproject.admin

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.UserManager
import android.provider.Settings
import com.example.sampleusbproject.MainActivity
import timber.log.Timber

class KioskManager(private val context: Context) {

    private val devicePolicyManager: DevicePolicyManager by lazy {
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val adminComponentName: ComponentName by lazy {
        KioskDeviceAdminReceiver.getComponentName(context)
    }

    /**
     * Включить режим киоска для устройства
     */
    fun enableKioskMode() {
        if (isDeviceOwner()) {
            // Разрешаем только наше приложение для запуска в режиме киоска
            val packages = arrayOf(context.packageName,"com.android.settings/.homepage.SettingsHomepageActivity")
            devicePolicyManager.setLockTaskPackages(adminComponentName, packages)

            // Настраиваем ограничения для пользователя
            setUserRestrictions()

            // Отключаем системные фичи, которые могут мешать режиму киоска
            disableSystemFeatures()

            // Отключаем экран блокировки
            devicePolicyManager.setKeyguardDisabled(adminComponentName, true)

            // Отключаем строку состояния
            devicePolicyManager.setStatusBarDisabled(adminComponentName, true)
        } else {
            throw SecurityException("Приложение не является Device Owner. Выполните команду ADB: adb shell dpm set-device-owner ${context.packageName}/.KioskDeviceAdminReceiver")
        }
    }

    /**
     * Запуск режима блокировки задачи (LockTask)
     * Должен вызываться из Activity
     */
    fun startLockTask(activity: Activity) {
        if (isDeviceOwner()) {
            activity.startLockTask()
        }
    }

    /**
     * Остановка режима блокировки задачи
     * Должен вызываться из Activity
     */
    fun stopLockTask(activity: Activity) {
        if (isDeviceOwner()) {
            activity.stopLockTask()
        }
    }

    /**
     * Установка ограничений для пользователя
     */
    private fun setUserRestrictions() {
        // Запрещаем безопасный режим загрузки
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_SAFE_BOOT)

        // Запрещаем сброс настроек
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_FACTORY_RESET)

        // Запрещаем добавление пользователей
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_ADD_USER)

        // Запрещаем монтирование физических носителей
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA)

        // Запрещаем регулировку громкости
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_ADJUST_VOLUME)

        // Запрещаем системные диалоги ошибок
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_SYSTEM_ERROR_DIALOGS)

        // Запрещаем использование USB-накопителей
        devicePolicyManager.addUserRestriction(adminComponentName, UserManager.DISALLOW_USB_FILE_TRANSFER)
    }

    /**
     * Отключение системных фич, которые могут мешать режиму киоска
     */
    private fun disableSystemFeatures() {
        // Отключаем экранные кнопки навигации (если поддерживается)
//        try {
//            devicePolicyManager.setGlobalSetting(
//                adminComponentName,
//                "navigation_bar_show",
//                "0"
//            )
//        } catch (e: SecurityException) {
//            Timber.e(e.localizedMessage)
//        }
        // Устанавливаем постоянное включение экрана при подключении к питанию
        devicePolicyManager.setGlobalSetting(
            adminComponentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            (BatteryManager.BATTERY_PLUGGED_AC or
                    BatteryManager.BATTERY_PLUGGED_USB or
                    BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
        )

        // Отключаем автоматическое обновление времени (опционально)
        devicePolicyManager.setGlobalSetting(
            adminComponentName,
            Settings.Global.AUTO_TIME,
            "0"
        )
    }

    /**
     * Проверка, является ли приложение Device Owner
     */
    fun isDeviceOwner(): Boolean {
        return devicePolicyManager.isDeviceOwnerApp(context.packageName)
    }

    /**
     * Проверка, находится ли устройство в режиме LockTask
     */
    fun isInLockTaskMode(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    /**
     * Настройка автозапуска приложения после перезагрузки
     */
    fun setAutoStartAfterReboot() {
        if (isDeviceOwner()) {
            val filter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }

            devicePolicyManager.addPersistentPreferredActivity(
                adminComponentName,
                filter,
                ComponentName(context.packageName, MainActivity::class.java.name)
            )
        }
    }
}