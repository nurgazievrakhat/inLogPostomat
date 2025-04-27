package com.example.sampleusbproject.domain.repositories

interface UsbCommunicationRepository {
    fun openConnection(): Boolean
    fun closeConnection()
    fun sendCommand(command: ByteArray): Result<ByteArray>
}