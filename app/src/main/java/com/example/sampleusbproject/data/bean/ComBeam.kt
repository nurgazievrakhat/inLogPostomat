package com.example.sampleusbproject.data.bean


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ComBean(
    val sComPort: String,
    val bRec: ByteArray,
    val sRecTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
) {
    constructor(sPort: String, buffer: ByteArray, size: Int) : this(
        sComPort = sPort,
        bRec = buffer.copyOf(size)
    )
}