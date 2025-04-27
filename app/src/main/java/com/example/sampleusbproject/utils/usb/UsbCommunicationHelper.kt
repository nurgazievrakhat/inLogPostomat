package com.example.sampleusbproject.utils.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Build
import timber.log.Timber

class UsbCommunicationHelper(
    private val context: Context,
    private val vendorId: Int,
    private val productId: Int,
) {
    companion object {
        private const val TAG = "UsbConnectionManager"
        private const val ACTION_USB_PERMISSION = "com.example.app.USB_PERMISSION"
    }

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var connection: UsbDeviceConnection? = null
    private var device: UsbDevice? = null
    private var usbInterface: UsbInterface? = null

    fun findDevice(): Boolean {
        val deviceList = usbManager.deviceList
        deviceList.values.forEach { device ->
            if (device.vendorId == vendorId && device.productId == productId) {
                this.device = device
                return true
            }
        }
        Timber.e("Device not found")
        return false
    }
    fun requestPermission(): Boolean {
        val device = this.device ?: run {
            Timber.e("No device found")
            return false
        }

        if (usbManager.hasPermission(device)) {
            return true
        }

        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent(ACTION_USB_PERMISSION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
        )
        usbManager.requestPermission(device, permissionIntent)

        // Результат будет получен через BroadcastReceiver
        return false
    }
    fun openConnection(): Boolean {
        val device = this.device ?: return false

        if (!usbManager.hasPermission(device)) {
            return false
        }

        connection = usbManager.openDevice(device).also { connection ->
            if (connection == null) {
                Timber.e( "Could not open connection")
                return false
            }
        }

        if (device.interfaceCount > 0) {
            usbInterface = device.getInterface(4).also { usbInterface ->
                if (connection?.claimInterface(usbInterface, true) == true) {
                    Timber.d("Interface claimed successfully")
                    return true
                } else {
                    Timber.e("Could not claim interface")
                    connection?.close()
                    connection = null
                }
            }
        }

        return false
    }

    fun closeConnection() {
        connection?.let { conn ->
            usbInterface?.let { intf ->
                conn.releaseInterface(intf)
            }
            conn.close()
            connection = null
            usbInterface = null
        }
    }

    fun sendCommand(command: ByteArray): Boolean {
        val conn = connection
        val intf = device?.getInterface(4)

        if (conn == null || intf == null) {
            Timber.e( "Not connected")
            return false
        }

        val endpointOut = (0 until intf.endpointCount)
            .map { intf.getEndpoint(it) }
            .find { it.direction == UsbConstants.USB_DIR_OUT }

        if (endpointOut == null) {
            Timber.e(TAG, "No output endpoint found")
            return false
        }

        val bytesTransferred = conn.bulkTransfer(endpointOut, command, command.size, 1000)
        return bytesTransferred >= 0
    }
    fun sendCommandAllInter(command: ByteArray): Boolean {
        val conn = connection
        val device = device

        if (conn == null || device == null) {
            Timber.e("Not connected")
            return false
        }

        // Перебираем все интерфейсы устройства
        for (interfaceIndex in 0 until device.interfaceCount) {
            val intf = device.getInterface(interfaceIndex)

            // Находим выходные эндпоинты в текущем интерфейсе
            val endpointsOut = (0 until intf.endpointCount)
                .map { intf.getEndpoint(it) }
                .filter { it.direction == UsbConstants.USB_DIR_OUT }

            // Пытаемся отправить команду через каждый выходной эндпоинт
            for (endpointOut in endpointsOut) {
                try {
                    val bytesTransferred = conn.bulkTransfer(endpointOut, command, command.size, 1000)
                    if (bytesTransferred >= 0) {
                        // Успешно отправили команду
                        Timber.d("Command sent successfully through interface $interfaceIndex")
                        return true
                    }
                } catch (e: Exception) {
                    Timber.e("Failed to send command through interface $interfaceIndex: ${e.message}")
                    // Продолжаем перебор, не прерываем цикл при ошибке
                }
            }
        }

        // Если не удалось отправить ни через один интерфейс
        Timber.e("Failed to send command through any interface")
        return false
    }

    fun receiveResponse(expectedLength: Int): ByteArray? {
        val conn = connection
        val intf = device?.getInterface(4)

        if (conn == null || intf == null) {
            Timber.e(TAG, "Not connected")
            return null
        }

        // Находим конечную точку для приема данных
        val endpointIn = (0 until intf.endpointCount)
            .map { intf.getEndpoint(it) }
            .find { it.direction == UsbConstants.USB_DIR_IN }

        if (endpointIn == null) {
            Timber.e(TAG, "No input endpoint found")
            return null
        }

        val buffer = ByteArray(expectedLength)
        val bytesReceived = conn.bulkTransfer(endpointIn, buffer, buffer.size, 1000)

        return when {
            bytesReceived <= 0 -> null
            bytesReceived < expectedLength -> buffer.copyOf(bytesReceived)
            else -> buffer
        }
    }
    fun receiveResponseAllInter(expectedLengthHint: Int = 64): ByteArray? {
        val conn = connection
        val device = device

        if (conn == null || device == null) {
            Timber.e(TAG, "Not connected")
            return null
        }

        // Перебираем все интерфейсы устройства
        for (interfaceIndex in 0 until device.interfaceCount) {
            val intf = device.getInterface(interfaceIndex)

            // Находим входные эндпоинты в текущем интерфейсе
            val endpointsIn = (0 until intf.endpointCount)
                .map { intf.getEndpoint(it) }
                .filter { it.direction == UsbConstants.USB_DIR_IN }

            // Пытаемся получить ответ через каждый входной эндпоинт
            for (endpointIn in endpointsIn) {
                try {
                    // Используем размер пакета из эндпоинта или переданный размер, если он больше
                    val bufferSize = maxOf(endpointIn.maxPacketSize, expectedLengthHint)
                    val buffer = ByteArray(bufferSize)

                    Timber.d("Trying to receive data from interface $interfaceIndex with buffer size $bufferSize")
                    val bytesReceived = conn.bulkTransfer(endpointIn, buffer, buffer.size, 1000)

                    if (bytesReceived > 0) {
                        // Успешно получили данные
                        Timber.d("Data received successfully through interface $interfaceIndex: $bytesReceived bytes")
                        return buffer.copyOf(bytesReceived)
                    } else {
                        Timber.d("No data received from interface $interfaceIndex, result: $bytesReceived")
                    }
                } catch (e: Exception) {
                    Timber.e("Failed to receive data through interface $interfaceIndex: ${e.message}")
                    // Продолжаем перебор, не прерываем цикл при ошибке
                }
            }
        }

        // Если не удалось получить данные ни через один интерфейс
        Timber.e("Failed to receive data through any interface")
        return null
    }
    fun getDevice(): UsbDevice? = device

    fun getUsbInterface(): UsbInterface? = usbInterface

    fun isConnected(): Boolean = connection != null
}