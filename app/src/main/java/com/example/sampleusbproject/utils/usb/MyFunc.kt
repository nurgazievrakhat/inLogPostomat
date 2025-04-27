package com.example.sampleusbproject.utils.usb

object MyFunc {
    fun isOdd(num: Int): Boolean = num and 1 == 1

    fun hexToInt(inHex: String): Int = inHex.toInt(16)

    fun hexToByte(inHex: String): Byte = inHex.toInt(16).toByte()

    fun byteToHex(inByte: Byte): String = "%02X".format(inByte)

    fun byteArrToHex(inBytArr: ByteArray): String =
        inBytArr.joinToString(" ") { byteToHex(it) }

    fun byteArrToHexMsg(inBytArr: ByteArray): String =
        inBytArr.joinToString("") { byteToHex(it) }

    fun byteArrToHex(inBytArr: ByteArray, offset: Int, byteCount: Int): String =
        inBytArr.slice(offset until (offset + byteCount)).joinToString("") { byteToHex(it) }

    fun hexToByteArr(inHex: String): ByteArray {
        var hex = inHex
        if (hex.length % 2 != 0) {
            hex = "0$hex"
        }
        return ByteArray(hex.length / 2) { i ->
            hexToByte(hex.substring(i * 2, i * 2 + 2))
        }
    }
}
