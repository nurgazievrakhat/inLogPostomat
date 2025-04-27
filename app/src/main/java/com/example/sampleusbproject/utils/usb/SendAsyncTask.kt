package com.example.sampleusbproject.utils.usb

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class SendAsyncTask {
    fun execute(jsonObject: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            doInBackground(jsonObject)
        }
    }

    private suspend fun doInBackground(jsonObject: JSONObject): Boolean {
        try {
            val jsonArray = jsonObject.optJSONArray("data") ?: return false
            val bData = ByteArray(jsonArray.length()) { i ->
                (jsonArray.getInt(i) and 0xFF).toByte()
            }

            SerialPortUtil.getSerialtty()?.getOutputStream()?.use { output ->
                output.write(bData)
                Log.e("socketMain", "bData ==>${jsonArray}")
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}