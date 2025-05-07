package com.example.sampleusbproject.admin

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.sampleusbproject.MainActivity

class KioskDeviceAdminReceiver : DeviceAdminReceiver() {

    // Вызывается, когда приложение получает права администратора устройства
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        showToast(context, "Права администратора устройства активированы")

        // Здесь можно выполнить дополнительную настройку, если необходимо
    }

    // Вызывается, когда права администратора отключаются
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        showToast(context, "Права администратора устройства деактивированы")
    }

    // Вызывается, когда Device Owner режим активирован
    // Примечание: этот метод вызывается только при назначении через ADB, а не через UI
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)

        // Автоматически активируем режим киоска при назначении Device Owner
        val kioskManager = KioskManager(context)
        kioskManager.enableKioskMode()

        // Запускаем основную активность
        val launchIntent = Intent(context, MainActivity::class.java)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
    }

    // Вспомогательный метод для отображения уведомлений
    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Статический класс-компаньон для удобного доступа
    companion object {
        // Получить ComponentName для этого receiver'а
        fun getComponentName(context: Context) = ComponentName(context, KioskDeviceAdminReceiver::class.java)

        // Проверить, является ли приложение Device Owner
        fun isDeviceOwner(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            return dpm.isDeviceOwnerApp(context.packageName)
        }
    }
}