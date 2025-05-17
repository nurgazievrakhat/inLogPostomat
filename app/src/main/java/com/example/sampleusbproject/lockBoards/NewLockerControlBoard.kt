package com.example.sampleusbproject.lockBoards

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.data.reposityImpl.OpenDoorResponse
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.domain.models.LockStatus
import com.example.sampleusbproject.utils.usb.SerialPortServer
import org.json.JSONObject
import timber.log.Timber

class NewLockerControlBoard(
    private val context: Context,
    private val devicePath: String = "/dev/ttyS3",
    private val baudRate: String = "9600",
    private val dataBits: String = "8",
    private val stopBits: String = "1",
    private val parity: String = "N"
) : LockerBoardInterface {
    private var serialPortServer: SerialPortServer? = null
    private var connected = false

    private val _eventLiveData = MutableLiveData<LockerBoardResponse>()


    private val callbackHandler = Handler(Looper.getMainLooper()) { msg ->
        Log.d("LockerBoard", "Callback: ${msg.obj}")

        val msgText = msg.obj?.toString()?.removePrefix("JsonBean") ?: return@Handler true

        try {
            val json = JSONObject(msgText)

            when (json.getString("scs_type")) {
                "READ_DOOR_TYPE" -> {
                    val board = json.getInt("dial_up")
                    val door = json.getInt("door_number")
                    val status = when (json.getString("msg")) {
                        "READ_DOOR_TYPE_ON" -> LockStatus.CLOSED
                        "READ_DOOR_TYPE_OFF" -> LockStatus.OPEN
                        else -> LockStatus.UNKNOWN
                    }
                    _eventLiveData.postValue(LockerBoardResponse.DoorStatus(board, door, status))
                }
                "READ_ALL_DOOR_TYPE" -> {
                    val board = json.getInt("dial_up")
                    val binary = json.getString("door_number")
                    _eventLiveData.postValue(LockerBoardResponse.AllDoorsStatus(board, binary))
                }
                "OPEN_DOOR" -> {
                    val board = json.getInt("dial_up")
                    val door = json.getInt("door_number")
                    val status = when (json.getString("status")) {
                        "1" -> OpenDoorResponse.SUCCESS
                        "2" -> OpenDoorResponse.FAILURE
                        "3" -> OpenDoorResponse.SHORT_CIRCUIT
                        else -> OpenDoorResponse.UNKNOWN
                    }
                    _eventLiveData.postValue(LockerBoardResponse.OpenDoor(board, door, status))
                }
                else -> {
                    _eventLiveData.postValue(LockerBoardResponse.Raw(msgText))
                }
            }
        } catch (e: Exception) {
            Log.e("LockerBoard", "Failed to parse JSON: ${e.message}")
            _eventLiveData.postValue(LockerBoardResponse.Error("Failed to parse JSON: ${e.message}"))
        }
        true
    }
    init {
        serialPortServer = SerialPortServer(devicePath, baudRate, dataBits, stopBits, parity, callbackHandler)
        connect()
    }
    override fun connect(): Boolean {
        if (isConnected()) {
            return true
        }
        return try {
            serialPortServer?.serialPortServerOpen()
            connected = true
            Log.e("LockerBoard", "Connection success")
            true
        } catch (e: Exception) {
            Log.e("LockerBoard", "Connection failed: ${e.message}")
            connected = false
            false
        }
    }

    override fun disconnect() {
        // Реализация закрытия соединения зависит от наличия метода закрытия в JAR
        serialPortServer?.closeComPort()
        serialPortServer = null
        connected = false
    }

    override fun isConnected(): Boolean = connected

    override fun openLocker(boardAddress: Int, lockerId: Int): Boolean {
        return try {
            serialPortServer?.spsOpenDoor(boardAddress, lockerId)
            true
        } catch (e: Exception) {
            Log.e("LockerBoard", "Failed to open locker: ${e.message}")
            false
        }
    }

    override fun openLockers(boardAddress: Int, lockerIds: IntArray): Boolean {
        try {
            for (id in lockerIds) {
                serialPortServer?.spsOpenDoor(boardAddress, id)
                Thread.sleep(200) // Интервал, рекомендованный в документации
            }
            return true
        } catch (e: Exception) {
            Log.e("LockerBoard", "Failed to open lockers: ${e.message}")
            return false
        }
    }

    override fun getEventLiveData(): LiveData<LockerBoardResponse> {
        return _eventLiveData
    }

    override fun changeLockTime(boardAddress: Int) {
        try {
            serialPortServer?.lockTypeUpload(boardAddress, "11")
//            serialPortServer?.spsReadLockTime(48)
        } catch (e: Exception) {
            Timber.e("changeLockTime $boardAddress ")
        }
    }

    override fun readLockTime(boardAddress: Int) {
        try {
            serialPortServer?.checkLockType(boardAddress)
//            serialPortServer?.spsReadLockTime(48)
        } catch (e: Exception) {
            Timber.e("readLockTime $boardAddress ")
        }
    }

    override fun getLockerStatus(boardAddress: Int, lockerId: Int): LockStatus {
        try {
            serialPortServer?.readLockType(boardAddress, lockerId)
//            serialPortServer?.spsReadLockTime(48)
        } catch (e: Exception) {
            Timber.e("READ_LOCK_TYPE $boardAddress $lockerId")
        }

        // Нужно будет внедрить обработку callback и парсинг JSON
        return LockStatus.UNKNOWN
    }

    override fun getAllLockersStatus(boardAddress: Int, totalLockers: Int): List<LockStatus?>? {
        // Отправка команды на получение состояния всех замков
        // serialPortServer?.someReadAllStatusCommand(boardAddress)

        // Здесь тоже нужно распарсить ответ типа:
        // "door_number":"111111111111111111111101"
        // Временно возвращаем null
        return null
    }
}