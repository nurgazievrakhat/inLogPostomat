package com.example.sampleusbproject.data.reposityImpl

import com.example.sampleusbproject.domain.repositories.UsbCommunicationRepository
import com.example.sampleusbproject.utils.usb.UsbCommunicationHelper
import javax.inject.Inject

class UsbCommunicationRepositoryImpl @Inject constructor(
    private val usbCommunicationHelper: UsbCommunicationHelper
) : UsbCommunicationRepository {

    override fun openConnection(): Boolean {
        return usbCommunicationHelper.openConnection()
    }

    override fun closeConnection() {

    }

    override fun sendCommand(command: ByteArray): Result<ByteArray> {
        return Result.success(byteArrayOf())
    }
}