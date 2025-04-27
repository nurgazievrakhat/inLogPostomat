package com.example.sampleusbproject.utils.usb


import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ReceiveAsyncTask {

    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            doInBackground()
        }
    }

    private suspend fun doInBackground(): Boolean {
        try {
            val serialtty = SerialPortUtil.getSerialtty()
            val inputStream = serialtty?.getInputStream()
            if (serialtty == null || inputStream == null) {
                return false
            }

            val buffer = ByteArray(1024)
            var len: Int

            while (inputStream.read(buffer, 0, buffer.size).also { len = it } > 0) {
                val obj = JSONObject().apply {
                    putOpt("time", System.currentTimeMillis())
                    putOpt("order_ary", JSONArray().apply {
                        for (i in 0 until len) {
                            put(buffer[i])
                        }
                    })
                }
                Log.e("socketMain", "receiver ==>${obj}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}