package com.example.sampleusbproject.utils.usb


import android.util.Log
import android_serialport_api.SerialPort
import com.example.sampleusbproject.data.bean.ComBean
import com.example.sampleusbproject.utils.usb.MyFunc.byteArrToHexMsg
import com.example.sampleusbproject.utils.usb.MyFunc.hexToByteArr
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidParameterException

abstract class SerialHelper(
    private var port: String = "/dev/s3c2410_serial0",
    private var baudRate: Int = 9600,
    private var dataBits: Int = 8,
    private var stopBits: Int = 1,
    private var parity: Char = 'N'
) {
    private var serialPort: SerialPort? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var readThread: ReadThread? = null
    private var sendThread: SendThread? = null
    private var isOpen = false
    private var loopData: ByteArray = byteArrayOf(48)
    private var delay: Int = 500
    private var portResult: String = ""
    private var portBuffer: String = ""

    // Configure serial parameters
    fun setN81(dataBits: String, stopBits: String, parity: String) {
        this.parity = parity.firstOrNull() ?: 'N'
        this.dataBits = dataBits.toIntOrNull() ?: 8
        this.stopBits = stopBits.toIntOrNull() ?: 1
    }

    @Throws(SecurityException::class, IOException::class, InvalidParameterException::class)
    fun open() {
        val device = File(port)
        if (!device.canRead() || !device.canWrite()) {
            try {
                val su = Runtime.getRuntime().exec("/system/bin/su")
                val cmd = "chmod 777 ${device.absolutePath}\nexit\n"
                su.outputStream.write(cmd.toByteArray())
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw SecurityException("Failed to set permissions for $port")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Permission error", e)
                throw SecurityException("Failed to set permissions", e)
            }
        }

        Log.d(TAG, "Opening port: $port, baud: $baudRate, $dataBits${parity}$stopBits")
        serialPort = SerialPort(
            File(port),
            baudRate,
            dataBits,
            stopBits,
            parity
        ).also {
            outputStream = it.getOutputStream()
            inputStream = it.getInputStream()
        }

        readThread = ReadThread().apply { start() }
        sendThread = SendThread().apply {
            setSuspendFlag()
            start()
        }
        isOpen = true
    }

    fun close() {
        readThread?.interrupt()
        readThread = null

        serialPort?.close()
        serialPort = null
        outputStream = null
        inputStream = null

        isOpen = false
    }

    fun send(data: ByteArray) {
        try {
            Log.d("SerialHelper", "Отправка команды (byte[]): " + MyFunc.byteArrToHex(data));
            outputStream?.write(data)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to send data", e)
        }
    }

    fun sendHex(hex: String) {
        Log.d("SerialHelper", "Отправка команды (hex): " + hex);
        send(hexToByteArr(hex))
    }

    fun sendText(text: String) {
        Log.d("SerialHelper", "Отправка команды (txt): " + text);
        send(text.toByteArray())
    }

    // Getters
    fun getBaudRate(): Int {
        Log.d(TAG, "Getting baud rate: $baudRate")
        return baudRate
    }

    fun getPort(): String = port

    fun isOpen(): Boolean = isOpen

    fun getLoopData(): ByteArray = loopData

    // Setters
    fun setBaudRate(baud: Int): Boolean {
        if (isOpen) return false
        baudRate = baud
        return true
    }

    fun setBaudRate(baud: String): Boolean {
        return baud.toIntOrNull()?.let { setBaudRate(it) } ?: false
    }
    fun setPort(newPort: String): Boolean {
        if (isOpen) return false
        port = newPort
        return true
    }

    fun setLoopData(data: ByteArray) {
        loopData = data
    }

    fun setTextLoopData(text: String) {
        loopData = text.toByteArray()
    }

    fun setHexLoopData(hex: String) {
        loopData = hexToByteArr(hex)
    }

    fun setDelay(newDelay: Int) {
        delay = newDelay
    }

    fun getDelay(): Int = delay

    fun startSend() {
        sendThread?.setResume()
    }

    fun stopSend() {
        sendThread?.setSuspendFlag()
    }

    protected abstract fun onDataReceived(data: String)

    private inner class SendThread : Thread() {
        private var suspendFlag = true

        override fun run() {
            while (!isInterrupted) {
                synchronized(this) {
                    while (suspendFlag) {
                        try {
                            (this as Object).wait()
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Send thread interrupted", e)
                            return
                        }
                    }
                }

                send(loopData)
                sleep(delay.toLong())
            }
        }

        fun setSuspendFlag() {
            suspendFlag = true
        }

        @Synchronized
        fun setResume() {
            suspendFlag = false
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
            (this as Object).notify()
        }
    }

    private inner class ReadThread : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    inputStream?.let { input ->

                        val buffer = ByteArray(1024)
                        val size = input.read(buffer)

                        if (size > 0) {
                            val comData = ComBean(port, buffer, size)
                            val received = byteArrToHexMsg(comData.bRec)
                            val rawData = byteArrToHexMsg(buffer.copyOf(size))
                            Log.d(TAG, "Raw data received: $rawData")

                            if (received.startsWith("73746172") && received.contains("656E64")) {
                                portResult = received.substring(
                                    received.indexOf("73746172"),
                                    received.lastIndexOf("656E64") + 4
                                )
                                if (portResult.indexOf("73746172") == 0) {
                                    onDataReceived(portResult)
                                } else {
                                    var remaining = portResult
                                    while (remaining.contains("73746172") && remaining.contains("656E64")) {
                                        val frame = remaining.substring(
                                            remaining.indexOf("73746172"),
                                            remaining.indexOf("656E64") + 4
                                        )
                                        onDataReceived(frame)
                                        remaining = remaining.substring(frame.length)
                                    }
                                }
                            } else {
                                portBuffer += received
                                if (portBuffer.startsWith("73746172") && portBuffer.endsWith("656E64")) {
                                    onDataReceived(portBuffer)
                                    portBuffer = ""
                                } else if (portBuffer.length > 24) {
                                    portBuffer = ""
                                }
                            }
                        }
                    } ?: return
                } catch (e: Throwable) {
                    Log.e(TAG, "Read thread error", e)
                    return
                }
            }
        }
    }

    companion object {
        private const val TAG = "SerialHelper"
    }
}