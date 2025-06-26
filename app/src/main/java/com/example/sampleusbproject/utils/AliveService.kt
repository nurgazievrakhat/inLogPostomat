package com.example.sampleusbproject.utils

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger

class AliveService : Service() {

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            MSG_PING -> {
                val reply = msg.replyTo
                val replyMsg = Message.obtain(null, MSG_PONG)
                replyMsg.arg1 = if (AppStatus.isInForeground) 1 else 0
                reply.send(replyMsg)
                true
            }
            else -> false
        }
    }

    private val messenger = Messenger(handler)

    override fun onBind(intent: Intent?): IBinder = messenger.binder

    companion object {
        const val MSG_PING = 1
        const val MSG_PONG = 2
    }
}

object AppStatus {
    @Volatile
    var isInForeground: Boolean = false
}