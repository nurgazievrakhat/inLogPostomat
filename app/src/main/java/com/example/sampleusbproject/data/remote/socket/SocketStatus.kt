package com.example.sampleusbproject.data.remote.socket

sealed class SocketStatus {
    object Connected : SocketStatus()
    data class Disconnected(val reason: String) : SocketStatus()
    data class Error(val error: String) : SocketStatus()
}