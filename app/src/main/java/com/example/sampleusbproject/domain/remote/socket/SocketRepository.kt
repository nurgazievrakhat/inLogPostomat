package com.example.sampleusbproject.domain.remote.socket

import com.example.sampleusbproject.data.remote.socket.SocketStatus
import com.example.sampleusbproject.domain.remote.socket.model.CellData
import com.example.sampleusbproject.domain.remote.socket.model.CellStatus
import com.example.sampleusbproject.domain.remote.socket.model.CommandInfo
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import kotlinx.coroutines.flow.Flow

interface SocketRepository {
    fun connect() : Flow<SocketStatus>
    fun disconnect()
    fun isConnected(): Boolean

    // События постомата
    fun onOpenPostomatCell(listener: (CellData) -> Unit)
    fun onSendPostomatInfo(listener: (PostomatInfo) -> Unit)
    fun onPostomatUpdated(listener: () -> Unit)

    // Команды постомата
    fun getPostomatInfo()
    fun getPostomatStatus()
    fun sendCellStatus(cellStatus: CellStatus)
    fun sendCommandInfo(commandInfo: CommandInfo)

    // Общие методы для работы с событиями
    fun on(event: String, listener: (Any) -> Unit)
    fun off(event: String)
    fun once(event: String, listener: (Any) -> Unit)
    fun emit(event: String, data: Any? = null)

    //room
    fun getPostamatsLocal(id: String) : Flow<List<PostomatInfo>>
}