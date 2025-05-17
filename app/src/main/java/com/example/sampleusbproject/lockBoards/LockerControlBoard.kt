package com.example.sampleusbproject.lockBoards

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sampleusbproject.data.LockerBoardResponse
import com.example.sampleusbproject.domain.interfaces.LockerBoardInterface
import com.example.sampleusbproject.domain.models.LockStatus
import com.example.sampleusbproject.utils.usb.UsbCommunicationHelper
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class LockerControlBoard(
    private val context: Context,
    private val vendorId: Int,
    private val productId: Int
) : LockerBoardInterface {
    companion object {
        private const val TAG = "LockerControlBoard"

        // Коды команд согласно документации
        private const val CMD_HEADER_OPEN_CLOSE: Byte = 0x8A.toByte()
        private const val CMD_HEADER_GET_STATUS: Byte = 0x80.toByte()
        private const val CMD_HEADER_GET_VERSION: Byte = 0x91.toByte()

        // Байты действий
        private const val ACTION_OPEN: Byte = 0x11.toByte()
        private const val ACTION_CLOSE: Byte = 0x00.toByte()
        private const val ACTION_STATUS: Byte = 0x33.toByte()

        // Статусы замков
        private const val STATUS_OPEN: Byte = 0x11.toByte()
        private const val STATUS_CLOSED: Byte = 0x00.toByte()
    }

    protected val connectionManager = UsbCommunicationHelper(context, vendorId, productId)

    override fun connect(): Boolean {
        if (!connectionManager.findDevice()) {
            Timber.e("Device not found")
            return false
        }

        if (!connectionManager.requestPermission()) {
            Timber.e("No permission")
            return false
        }

        if (!connectionManager.openConnection()) {
            Timber.e("Failed to open connection")
            return false
        }

        return true
    }

    override fun disconnect() {
        connectionManager.closeConnection()
    }

    override fun isConnected(): Boolean {
        return connectionManager.isConnected()
    }

    override fun openLocker(boardAddress: Int,lockerId: Int): Boolean {
        if (!isConnected() || connectionManager.getUsbInterface() == null) {
            Timber.w("USB interface is null, reconnecting...")
            disconnect()
            if (!connect()) {
                Timber.e("Failed to reconnect")
                return false
            }
        }

        val command = buildCommand(
            CMD_HEADER_OPEN_CLOSE,
            boardAddress.toByte(),
            lockerId.toByte(),
            ACTION_OPEN
        )

        val commandSent = connectionManager.sendCommand(command)
        if (!commandSent) {
            Timber.e("Failed to send open command for locker: $lockerId")
            return false
        }

        val response = connectionManager.receiveResponse(5) // Ожидаем ответ из 5 байт
        if (response == null || response.size < 5) {
            Timber.e( "Invalid response for opening locker: $lockerId")
            return false
        }

        // Проверяем, что ответ соответствует команде и статус "открыто"
        return response[0] == CMD_HEADER_OPEN_CLOSE &&
                response[3] == STATUS_OPEN
    }

    override fun openLockers(boardAddress: Int,lockerIds: IntArray): Boolean {
        if (lockerIds.isEmpty()) {
            return false
        }

        var allSuccess = true
        for (lockerId in lockerIds) {
            val success = openLocker(boardAddress, lockerId)
            if (!success) {
                allSuccess = false
                Timber.e("Failed to open locker: $lockerId")
            }
        }

        return allSuccess
    }

    override fun getEventLiveData(): LiveData<LockerBoardResponse> {
        return MutableLiveData<LockerBoardResponse>()
    }


    override fun getLockerStatus(boardAddress: Int, lockerId: Int): LockStatus {
        if (!isConnected()) {
            connect()
            Timber.e(TAG, "Not connected")
            return LockStatus.UNKNOWN
        }

        val command = buildCommand(
            CMD_HEADER_GET_STATUS,
            boardAddress.toByte(),
            lockerId.toByte(),
            ACTION_STATUS
        )
        val commandSent = connectionManager.sendCommand(command)
        if (!commandSent) {
            Timber.e("Failed to send status command for locker: $lockerId")
            return LockStatus.UNKNOWN
        }

        val response = connectionManager.receiveResponse(5)
        if (response == null || response.size < 5) {
            Timber.e("Invalid response for locker status: $lockerId")
            return LockStatus.UNKNOWN
        }
        Timber.e(response.joinToString { it.toUByte().toString(16) })
        return when (response[3]) {
            STATUS_OPEN -> LockStatus.OPEN
            STATUS_CLOSED -> LockStatus.CLOSED
            else -> LockStatus.UNKNOWN
        }
    }

    override fun readLockTime(boardAddress: Int) {

    }

    override fun changeLockTime(boardAddress: Int) {
        TODO("Not yet implemented")
    }

    override fun getAllLockersStatus(boardAddress: Int, totalLockers: Int): List<LockStatus> {
        if (!isConnected()) {
            Timber.e(TAG, "Not connected")
            return List(totalLockers) {LockStatus.UNKNOWN}
        }

        // Запрос статуса всех замков (lockerId = 0x00)
        val command = buildCommand(
            CMD_HEADER_GET_STATUS,
            boardAddress.toByte(),
            0x00.toByte(),
            ACTION_STATUS
        )

        val commandSent = connectionManager.sendCommand(command)
        if (!commandSent) {
            Timber.e("Failed to send status command for all lockers")
            return List(totalLockers) { LockStatus.UNKNOWN }
        }

        // Ожидаем ответ: заголовок + адрес платы + статусы всех замков + XOR
        val expectedResponseSize = 3 + totalLockers + 1
        val response = connectionManager.receiveResponse(expectedResponseSize)
        if (response == null || response.size < expectedResponseSize) {
            Timber.e("Invalid response for all lockers status")
            return List(totalLockers) { LockStatus.UNKNOWN }
        }

        // Парсим статусы замков (начиная с 3-го байта)
        return response.drop(2).take(totalLockers).map {
            when (it) {
                STATUS_OPEN -> LockStatus.OPEN
                STATUS_CLOSED -> LockStatus.CLOSED
                else -> LockStatus.UNKNOWN
            }
        }
    }

    private fun calculateXor(bytes: ByteArray): Byte {
        return bytes.fold(0) { acc, byte -> acc xor byte.toInt() }.toByte()
    }
    private fun calculateChecksum(data: ByteArray): Byte {
        return data.fold(0) { acc, byte -> acc xor (byte.toInt() and 0xFF) }.toByte()
    }

    private fun buildCommand(header: Byte, boardAddress: Byte, lockAddress: Byte, action: Byte): ByteArray {
        val cmd = byteArrayOf(header, boardAddress, lockAddress, action)
        val xor = calculateChecksum(cmd)
        return cmd + xor
    }
    fun getProtocolVersion(boardAddress: Int): Int? {
        if (!isConnected()) {
            Timber.e("Not connected")
            return null
        }

        val command = byteArrayOf(
            CMD_HEADER_GET_VERSION,
            boardAddress.toByte(),
            0xFE.toByte(),
            0xFE.toByte()
        )
        val xor = calculateXor(command)
        val fullCommand = command + xor

        val commandSent = connectionManager.sendCommand(fullCommand)
        if (!commandSent) {
            Timber.e("Failed to send version command")
            return null
        }

        val response = connectionManager.receiveResponse(5)
        if (response == null || response.size < 5) {
            Timber.e("Invalid response for version")
            return null
        }

        // Ответ: 0x91 [адрес] 0x6D [версия] [XOR]
        return if (response[0] == CMD_HEADER_GET_VERSION && response[2] == 0x6D.toByte()) {
            response[3].toInt() and 0xFF
        } else {
            null
        }
    }
}