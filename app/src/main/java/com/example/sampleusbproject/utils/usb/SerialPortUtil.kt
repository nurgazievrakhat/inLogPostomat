package com.example.sampleusbproject.utils.usb

import android_serialport_api.SerialPort
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

object SerialPortUtil {
    private const val TAG = "SerialPortUtil"
    private const val devPath = "/dev/ttyS2"
    private const val baudrate = 9600
    private var serialtty: SerialPort? = null
    private val orderList = CopyOnWriteArrayList<String>()
    private const val MSG_DOING = 101

    fun getSerialtty(): SerialPort? = serialtty

    private fun getParity(parity: String?): String = when (parity?.lowercase()) {
        null, "", "no" -> "n"
        "odd" -> "o"
        "even" -> "e"
        else -> "n"
    }

    fun addOrderList(result: String) {
        orderList.add(result)
    }

    private fun initSendSerial(obj: JSONObject) {
        SendAsyncTask().execute(obj)
    }

    fun initReceiveSerial() {
        ReceiveAsyncTask().execute()
    }
}