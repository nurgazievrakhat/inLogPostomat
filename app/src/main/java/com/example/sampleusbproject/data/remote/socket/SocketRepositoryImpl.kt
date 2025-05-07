package com.example.sampleusbproject.data.remote.socket

import android.util.Log
import com.example.sampleusbproject.data.PostomatInfoMapper
import com.example.sampleusbproject.domain.remote.socket.SocketEvents
import com.example.sampleusbproject.domain.remote.socket.SocketRepository
import com.example.sampleusbproject.domain.remote.socket.model.CellData
import com.example.sampleusbproject.domain.remote.socket.model.CellStatus
import com.example.sampleusbproject.domain.remote.socket.model.CommandInfo
import com.example.sampleusbproject.domain.remote.socket.model.PostomatInfo
import com.example.sampleusbproject.utils.CommonPrefs
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

class SocketRepositoryImpl @Inject constructor(
    private val prefs: CommonPrefs,
    private val postomatInfoMapper: PostomatInfoMapper
) : SocketRepository {
    private var socket: Socket? = null
    private val eventListeners = mutableMapOf<String, MutableList<(Any) -> Unit>>()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()

    override fun connect(): Flow<SocketStatus> = callbackFlow {
        if (socket?.connected() == true) {
            disconnect()
        }

        val opts = IO.Options().apply {
            forceNew = true
            reconnection = true
            timeout = 10000
            auth = mapOf("token" to prefs.getAccessToken())
        }

        socket = IO.socket("https://postomat-3.ooba.kg/postomat", opts)

        socket?.let { socket ->
            // Обработчики
            val onConnect = Emitter.Listener {
                trySend(SocketStatus.Connected)
                println("Socket.IO connected")
            }

            val onDisconnect = Emitter.Listener { args ->
                val reason = if (args.isNotEmpty()) args[0].toString() else "unknown"
                trySend(SocketStatus.Disconnected(reason))
                println("Socket.IO disconnected: $reason")
            }

            val onError = Emitter.Listener { args ->
                val error = if (args.isNotEmpty()) args[0].toString() else "unknown"
                trySend(SocketStatus.Error(error))
                println("Socket.IO connection error: $error")
            }

            // Подписка
            socket.on(Socket.EVENT_CONNECT, onConnect)
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
            socket.on(Socket.EVENT_CONNECT_ERROR, onError)

            // Восстановление кастомных слушателей
            eventListeners.forEach { (event, listeners) ->
                listeners.forEach { listener ->
                    registerSocketListener(event, listener)
                }
            }

            socket.connect()

            // Завершение потока
            awaitClose {
                socket.off(Socket.EVENT_CONNECT, onConnect)
                socket.off(Socket.EVENT_DISCONNECT, onDisconnect)
                socket.off(Socket.EVENT_CONNECT_ERROR, onError)
                disconnect()
            }
        }
    }
    override fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    override fun isConnected(): Boolean {
        return socket?.connected() == true
    }

    fun socketTest() {
        socket?.emit("POSTOMAT_GET_INFO",null,{
            Timber.e(it.toString())
        })
    }
    // Методы для работы с событиями постомата
    override fun onOpenPostomatCell(listener: (CellData) -> Unit) {
        val wrappedListener: (Any) -> Unit = { data ->
            val cellData = parseData(data, CellData::class.java)
            scope.launch {
                withContext(Dispatchers.Main) {
                    listener(cellData)
                }
            }
        }

        registerSocketListener(SocketEvents.OPEN_POSTOMAT_CELL, wrappedListener)
        addEventListenerToMap(SocketEvents.OPEN_POSTOMAT_CELL, wrappedListener)
    }

    override fun onSendPostomatInfo(listener: (PostomatInfo) -> Unit) {
        val wrappedListener: (Any) -> Unit = { data ->
            val info = parseData(data, PostomatInfo::class.java)
            scope.launch {
                withContext(Dispatchers.Main) {
                    postomatInfoMapper.savePostomatInfo(postomatInfo = info)
                    listener(info)
                }
            }
        }

        registerSocketListener(SocketEvents.SEND_POSTOMAT_INFO, wrappedListener)
        addEventListenerToMap(SocketEvents.SEND_POSTOMAT_INFO, wrappedListener)
    }

    override fun onPostomatUpdated(listener: () -> Unit) {
        socket?.on(SocketEvents.POSTOMAT_UPDATED) {
            scope.launch {
                withContext(Dispatchers.Main) {
                    listener()
                }
            }
        }

        // Сохраняем слушатель
        val wrappedListener: (Any) -> Unit = { _ -> listener() }
        addEventListenerToMap(SocketEvents.POSTOMAT_UPDATED, wrappedListener)
    }

    // Методы для отправки команд постомату
    override fun getPostomatInfo() {
        emit(SocketEvents.POSTOMAT_GET_INFO)
    }

    override fun getPostomatStatus() {
        emit(SocketEvents.POSTOMAT_STATUS)
    }

    override fun sendCellStatus(cellStatus: CellStatus) {
        emit(SocketEvents.POSTOMAT_CELL_STATUS, cellStatus)
    }

    override fun sendCommandInfo(commandInfo: CommandInfo) {
        emit(SocketEvents.POSTOMAT_COMMAND_INFO, commandInfo)
    }

    // Общие методы для работы с событиями
    override fun on(event: String, listener: (Any) -> Unit) {
        registerSocketListener(event, listener)
        addEventListenerToMap(event, listener)
    }

    override fun off(event: String) {
        socket?.off(event)
        eventListeners.remove(event)
    }

    override fun once(event: String, listener: (Any) -> Unit) {
        socket?.once(event) { args ->
            if (args.isNotEmpty()) {
                val data = args[0]
                scope.launch {
                    withContext(Dispatchers.Main) {
                        listener(data)
                    }
                }
            }
        }
    }

    override fun emit(event: String, data: Any?) {
        if (!isConnected()) {
            Timber.e("socket is not connected")
        }
        Timber.w(event)
        when (data) {
            null -> socket?.emit(event,null,{
                Timber.e( "emit: it")
            })
            is JSONObject -> socket?.emit(event, data)
            else -> {
                val jsonObject = JSONObject(gson.toJson(data))
                socket?.emit(event, jsonObject)
            }
        }
    }

    override fun getPostamatsLocal(id: String): Flow<List<PostomatInfo>> = flow {
         postomatInfoMapper.getPostomatInfo(id)
    }

    // Вспомогательные методы
    private fun registerSocketListener(event: String, listener: (Any) -> Unit) {
        Timber.d("Registering socket listener for event: $event")
        socket?.on(event) { args ->
            Timber.d("Received socket event: $event with args: ${args.joinToString()}")
            if (args.isNotEmpty()) {
                val data = args[0]
                scope.launch {
                    withContext(Dispatchers.Main) {
                        listener(data)
                    }
                }
            }
        }
    }

    private fun addEventListenerToMap(event: String, listener: (Any) -> Unit) {
        if (!eventListeners.containsKey(event)) {
            eventListeners[event] = mutableListOf()
        }
        eventListeners[event]?.add(listener)
    }

    // Обобщенный метод для парсинга данных без использования reified
    private fun <T> parseData(data: Any, clazz: Class<T>): T {
        return when {
            clazz.isInstance(data) -> clazz.cast(data)
            data is JSONObject -> gson.fromJson(data.toString(), clazz)
            else -> try {
                gson.fromJson(data.toString(), clazz)
            } catch (e: Exception) {
                throw IllegalArgumentException("Cannot parse data to ${clazz.simpleName}", e)
            }
        }
    }

    // Обобщенный метод для парсинга данных с использованием TypeToken для коллекций
    private fun <T> parseData(data: Any, type: Type): T {
        return try {
            gson.fromJson(data.toString(), type)
        } catch (e: Exception) {
            throw IllegalArgumentException("Cannot parse data to the specified type", e)
        }
    }
}