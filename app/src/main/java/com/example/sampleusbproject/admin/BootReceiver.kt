package com.example.sampleusbproject.admin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.sampleusbproject.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_REBOOT,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                scheduleAppLaunch(context)
            }
        }
    }

    private fun scheduleAppLaunch(context: Context) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(launchIntent)
    }
}