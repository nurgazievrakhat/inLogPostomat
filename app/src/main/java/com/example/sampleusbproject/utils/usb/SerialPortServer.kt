package com.example.sampleusbproject.utils.usb


import android.os.Handler
import android.os.Message
import android.util.Log
import android_serialport_api.SerialPort
import com.example.sampleusbproject.data.bean.ComBean
import com.example.sampleusbproject.data.bean.JsonBean
import timber.log.Timber
import java.io.IOException
import java.math.BigInteger
import java.security.InvalidParameterException
import java.util.*
import kotlin.collections.ArrayDeque

class SerialPortServer(
    private val sPort: String? = null,
    private val iBaudRate: String? = null,
    private val dataBits: String? = null,
    private val stopBits: String? = null,
    private val parity: String? = null,
    private val mHandler: Handler? = null
) {
    private val isHandlerOk = mHandler != null
    private var mSerialPort: SerialPort? = null
    private val ComA = SerialControl()
    private val dispQueue = DispQueueThread()

    init {
        sPort?.let { ComA.setPort(it) }
        dataBits?.let { stopBits?.let { sb -> parity?.let { p -> ComA.setN81(it, sb, p) } } }
        iBaudRate?.let { ComA.setBaudRate(it) }
    }

    fun serialPortServerOpen() = openComPort(ComA)

    fun spsOpenDoor(dialUp: Int, doorNum: Int) = sendPortData(dialUp, doorNum)

    fun spsChangeLockTime(dialUp: Int, times: Int) {
        if (times in 200..2000) {
            sendPortData1(changeMsgTime(getChangeDi(dialUp.toHex()), getChangeTime(times.toHex())))
        }
    }

    fun spsReadLockTime(dialUp: Int) = sendPortData1(readMsgTime(getChangeDi(dialUp.toHex())))

    fun readLockType(dialUp: Int, doorNum: Int) = sendPortDataLock(dialUp, doorNum)

    fun readLockType32(dialUp: Int) = sendPortDataLock32(dialUp)

    fun readLockType16(dialUp: Int) = sendPortDataLock16(dialUp)

    fun lockTypeUpload(dialUp: Int, type: String) = sendLockTypeUpload(dialUp, type)

    fun checkLockType(dialUp: Int) = sendCheckTypeUpload(dialUp)

    fun spsOpenAll(dialUp: Int) = sendPortData1(changeMsgAll(getChangeDi(dialUp.toHex())))

    private fun sendPortDataLock(sOut: Int, doorNum: Int) {
        if (ComA.isOpen()) {
            ComA.sendHex(readLockType(getChangeDi(sOut.toHex()), getChangeDi(doorNum.toHex())))
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    private fun sendPortDataLock32(sOut: Int) {
        if (ComA.isOpen()) {
            ComA.sendHex(readLockType32(getChangeDi(sOut.toHex())))
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    private fun sendPortDataLock16(sOut: Int) {
        if (ComA.isOpen()) {
            ComA.sendHex(readLockType16(getChangeDi(sOut.toHex())))
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }


    private fun sendCheckTypeUpload(dialUp: Int) {
        if (ComA.isOpen()) {
            ComA.sendHex(checkLockType(getChangeDi(dialUp.toHex())))
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    private fun sendLockTypeUpload(dialUp: Int, type: String) {
        if (ComA.isOpen()) {
            val sendHexMsg = lockTypeUpload(getChangeDi(dialUp.toHex()), type)
            Log.e("AutoFeedbackTest", "sendLockTypeUpload: $sendHexMsg")
            ComA.sendHex(sendHexMsg)
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    private fun sendPortData(sOut: Int, doorNum: Int) {
        if (ComA.isOpen()) {
            ComA.sendHex(changeMsgDoor(getChangeDi(sOut.toHex()), getChangeDi(doorNum.toHex())))
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    private fun sendPortData1(sOut: String) {
        if (ComA.isOpen()) {
            ComA.sendHex(sOut)
        } else {
            Log.e("SerialPortJar", "sendPortData: Serial port send failed")
        }
    }

    fun closeComPort() {
        ComA.stopSend()
        ComA.close()
        if (isHandlerOk) {
            try {
                mHandler?.sendMessage(Message().apply {
                    obj = errorMsg("Current serial port closed successfully!!!")
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openComPort(comPort: SerialHelper) {
        try {
            comPort.open()
        } catch (e: SecurityException) {
            if (isHandlerOk) {
                mHandler?.sendMessage(Message().apply {
                    obj = scsCallBack("501", "OPEN_SP", "", "", "PERMISSION_DENIED", "")
                })
            }
        } catch (e: IOException) {
            if (isHandlerOk) {
                mHandler?.sendMessage(Message().apply {
                    obj = scsCallBack("502", "OPEN_SP", "", "", "IOEXCEPTION", "")
                })
            }
        } catch (e: InvalidParameterException) {
            if (isHandlerOk) {
                mHandler?.sendMessage(Message().apply {
                    obj = scsCallBack("503", "OPEN_SP", "", "", "InvalidParameterException", "")
                })
            }
        }
    }

    companion object {
        fun dispRecData(comRecData: ComBean): String {
            return MyFunc.byteArrToHex(comRecData.bRec)
        }

        private fun showToast(msg: String) {
//            Toast.makeText(MyApp.instance, msg, Toast.LENGTH_SHORT).show()
            Timber.e(msg)
        }
    }

    private fun errorMsg(errorMsg: String): String {
        val error = errorMsg.substring(8, 10)
        return when (error.uppercase(Locale.getDefault())) {
            "8A" -> processDoorState(errorMsg)
            "7B" -> scsCallBack("3", "OPEN_DOOR", hex16to10(errorMsg.substring(10, 12)),
                hex16to10(errorMsg.substring(12, 14)), "SHORT_CIRCUIT", "")
            "6B" -> scsCallBack("4", "OPEN_ALL", hex16to10(errorMsg.substring(10, 12)),
                "", "OPEN_ALL_FAILURE", "")
            "90" -> scsCallBack("5", "OPEN_ALL", hex16to10(errorMsg.substring(10, 12)),
                "", "OPEN_ALL_SUCCESS", "")
            "1A" -> scsCallBack("6", "SET_LIGHT_TIME", hex16to10(errorMsg.substring(10, 12)),
                "", "SET_LIGHT_TIME_SUCCESS", "")
            "1B" -> scsCallBack("7", "GET_LIGHT_TIME", hex16to10(errorMsg.substring(10, 12)),
                "", "GET_LIGHT_TIME_SUCCESS", hex16to10(errorMsg.substring(12, 16)))
            "80", "60" -> processDoorType(errorMsg)
            "81" -> scsCallBack("15", "READ_ALL_DOOR_TYPE_32", hex16to10(errorMsg.substring(10, 12)),
                hex16to2(errorMsg.substring(12, 14)) + hex16to2(errorMsg.substring(14, 16)) +
                        hex16to2(errorMsg.substring(16, 18)) + hex16to2(errorMsg.substring(18, 20)),
                "READ_ALL_DOOR_TYPE_32", "")
            "83" -> scsCallBack("16", "READ_ALL_DOOR_TYPE_16", hex16to10(errorMsg.substring(10, 12)),
                hex16to2(errorMsg.substring(12, 14)) + hex16to2(errorMsg.substring(14, 16)),
                "READ_ALL_DOOR_TYPE_16", "")
            "2C" -> processLockUpload(errorMsg)
            "3D" -> processCheckLock(errorMsg)
            else -> scsCallBack("500", "", "", "", errorMsg, "")
        }
    }

    private fun processDoorState(errorMsg: String): String {
        val state = errorMsg.substring(14, 16)
        return when (state) {
            "11" -> scsCallBack("1", "OPEN_DOOR", hex16to10(errorMsg.substring(10, 12)),
                hex16to10(errorMsg.substring(12, 14)), "OPEN_DOOR_SUCCESS", "")
            "00" -> scsCallBack("2", "OPEN_DOOR", hex16to10(errorMsg.substring(10, 12)),
                hex16to10(errorMsg.substring(12, 14)), "OPEN_DOOR_FAILURE", "")
            else -> scsCallBack("500", "", "", "", errorMsg, "")
        }
    }

    private fun processDoorType(errorMsg: String): String {
        return if (errorMsg.length == 22) {
            val state1 = errorMsg.substring(14, 16)
            when (state1) {
                "00" -> scsCallBack("8", "READ_DOOR_TYPE", hex16to10(errorMsg.substring(10, 12)),
                    hex16to10(errorMsg.substring(12, 14)), "READ_DOOR_TYPE_OFF", "")
                "11" -> scsCallBack("9", "READ_DOOR_TYPE", hex16to10(errorMsg.substring(10, 12)),
                    hex16to10(errorMsg.substring(12, 14)), "READ_DOOR_TYPE_ON", "")
                else -> scsCallBack("500", "", "", "", errorMsg, "")
            }
        } else {
            val binaryStr = (12 until 20 step 2).joinToString("") {
                hex16to2(errorMsg.substring(it, it + 2))
            }
            scsCallBack("10", "READ_ALL_DOOR_TYPE", hex16to10(errorMsg.substring(10, 12)),
                binaryStr, if (errorMsg.length == 26) "READ_ALL_DOOR_TYPE" else "READ_ALL_DOOR_TYPE_24", "")
        }
    }

    private fun processLockUpload(errorMsg: String): String {
        val state1 = errorMsg.substring(14, 16)
        return if (state1 == "11") {
            scsCallBack("11", "CHANGE_LOCK_UPLOAD", hex16to10(errorMsg.substring(10, 12)),
                "", "CHANGE_LOCK_UPLOAD_ON", "")
        } else {
            scsCallBack("12", "CHANGE_LOCK_UPLOAD", hex16to10(errorMsg.substring(10, 12)),
                "", "CHANGE_LOCK_UPLOAD_OFF", "")
        }
    }

    private fun processCheckLock(errorMsg: String): String {
        val state3 = errorMsg.substring(14, 16)
        return if (state3 == "11") {
            scsCallBack("13", "CHECK_LOCK_UPLOAD", hex16to10(errorMsg.substring(10, 12)),
                "", "CHECK_LOCK_UPLOAD_ON", "")
        } else {
            scsCallBack("14", "CHECK_LOCK_UPLOAD", hex16to10(errorMsg.substring(10, 12)),
                "", "CHECK_LOCK_UPLOAD_OFF", "")
        }
    }

    private fun Int.toHex() = Integer.toHexString(this)

    private fun hex16to10(strHex: String) = BigInteger(strHex, 16).toString()

    private fun hex16to2(strHex: String): String {
        val integer = strHex.toInt(16)
        var two = integer.toString(2)
        two = "0".repeat(8 - two.length) + two
        return two.map { if (it == '0') '1' else '0' }.joinToString("")
    }

    private fun changeMsgTime(dialUp: String, timer: String) =
        "737461721A${dialUp}${timer}${getChangeDi(xor("1A${dialUp}${timer}"))}656E646F"

    private fun readMsgTime(dialUp: String) =
        "737461721B${dialUp}0000${getChangeDi(xor("1B${dialUp}0000"))}656E646F"

    private fun changeMsgDoor(banzi: String, door: String) =
        "737461728A${banzi}${door}11${getChangeDi(xor("8A${banzi}${door}11"))}656E646F"

    private fun changeMsgAll(numb: String) =
        "7374617290${numb}${getChangeDi(xor("90${numb}"))}656E646F"

    private fun readLockType(banzi: String, door: String) =
        "7374617280${banzi}${door}33${getChangeDi(xor("80${banzi}${door}33"))}656E646F"

    private fun readLockType32(banzi: String) =
        "7374617281${banzi}0033${getChangeDi(xor("81${banzi}0033"))}656E646F"

    private fun readLockType16(banzi: String) =
        "7374617283${banzi}0033${getChangeDi(xor("83${banzi}0033"))}656E646F"

    private fun lockTypeUpload(banzi: String, type: String) =
        "737461722C${banzi}00${type}${getChangeDi(xor("2C${banzi}00${type}"))}656E646F"

    private fun checkLockType(banzi: String) =
        "737461723D${banzi}0000${getChangeDi(xor("3D${banzi}0000"))}656E646F"

    private fun xor(content: String): String {
        val bytes = content.chunked(2).map { it.toInt(16) }
        var result = 0
        bytes.forEach { result = result xor it }
        return "%02X".format(result)
    }

    private fun getChangeTime(times: String) = times.padStart(4, '0')

    private fun getChangeDi(di: String) = di.padStart(2, '0')

    private fun scsCallBack(status: String, scsType: String, dialUp: String,
                            doorNumber: String, msg: String, times: String): String {
        return JsonBean().apply {
            this.status = status
            this.dial_up = dialUp
            this.door_number = doorNumber
            this.msg = msg
            this.scs_type = scsType
            this.times = times
        }.toString()
    }

    private inner class DispQueueThread : Thread() {
        private val queueList = ArrayDeque<ComBean>()

        override fun run() {
            while (!isInterrupted) {
                queueList.removeFirstOrNull()?.let {
                    // Process data here if needed
                }
            }
        }

        fun addQueue(comData: ComBean) {
            queueList.addLast(comData)
        }
    }
    fun testConnection() {
        if (ComA.isOpen()) {
            // Send a simple echo command if your device supports it
            ComA.sendHex("737461725454000000AA656E646F") // Example test command
            Log.d("SerialDebug", "Echo test sent")
        } else {
            Log.e("SerialDebug", "Cannot test - port not open")
        }
    }

    private inner class SerialControl : SerialHelper() {
        override fun onDataReceived(comRecData: String) {
            Log.d("SerialHelper", "Получен ответ: " + comRecData);
            if (isHandlerOk) {
                mHandler?.sendMessage(Message().apply {
                    obj = errorMsg(comRecData)
                })
            }
        }
    }
}