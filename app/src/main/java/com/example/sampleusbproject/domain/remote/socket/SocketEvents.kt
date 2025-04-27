package com.example.sampleusbproject.domain.remote.socket

object SocketEvents {
    // in
    const val OPEN_POSTOMAT_CELL = "OPEN_POSTOMAT_CELL"
    const val SEND_POSTOMAT_INFO = "SEND_POSTOMAT_INFO"
    const val POSTOMAT_UPDATED = "UPDATED_POSTOMAT"

    // out
    const val POSTOMAT_GET_INFO = "POSTOMAT_GET_INFO"
    const val POSTOMAT_STATUS = "POSTOMAT_STATUS"
    const val POSTOMAT_CELL_STATUS = "POSTOMAT_CELL_STATUS"
    const val POSTOMAT_COMMAND_INFO = "POSTOMAT_COMMAND_INFO"
}