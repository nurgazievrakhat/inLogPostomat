package com.example.sampleusbproject.utils

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class AliveService : Service() {
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): AliveService = this@AliveService
    }

    override fun onBind(intent: Intent?): IBinder = binder
}