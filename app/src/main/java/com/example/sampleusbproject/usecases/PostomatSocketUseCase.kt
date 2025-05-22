package com.example.sampleusbproject.usecases

import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.domain.remote.socket.SocketRepository
import com.example.sampleusbproject.domain.remote.socket.model.CellData
import com.example.sampleusbproject.domain.remote.socket.model.CellStatus
import com.example.sampleusbproject.domain.remote.socket.model.CommandInfo
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import javax.inject.Inject

class PostomatSocketUseCase @Inject constructor(
    private val socketRepository: SocketRepository
) {
    fun connectToPostomatServer() = socketRepository.connect()

    fun disconnectFromPostomatServer() {
        socketRepository.disconnect()
    }

    fun requestPostomatInfo() {
        socketRepository.getPostomatInfo()
    }

    fun requestPostomatStatus() {
        socketRepository.getPostomatStatus()
    }

    fun updateCellStatus(cellId: String, isOpened: Boolean) {
        val cellStatus = CellStatus(cellId, isOpened)
        socketRepository.sendCellStatus(cellStatus)
    }

    fun sendCellCommand(cellId: String, boardId: String, isOpened: Boolean) {
        val commandInfo = CommandInfo(cellId, boardId, isOpened)
        socketRepository.sendCommandInfo(commandInfo)
    }

    fun observePostomatCellEvents(onCellEvent: (CellData) -> Unit) {
        socketRepository.onOpenPostomatCell(onCellEvent)
    }

    fun observePostomatInfo(onInfoReceived: (PostomatInfo) -> Unit) {
        socketRepository.onSendPostomatInfo(onInfoReceived)
    }

    fun observePostomatUpdates(onUpdated: () -> Unit) {
        socketRepository.onPostomatUpdated(onUpdated)
    }

    suspend fun getPostamatCellLocal(id: String) = socketRepository.getPostamatsLocal(id)
    fun observePostamatCellLocal() = socketRepository.observePostamatsLocal()
    fun isConnected() = socketRepository.isConnected()

    fun sendOnceEvent(event: String, listener: (Any) -> Unit) = socketRepository.once(event,listener)

}