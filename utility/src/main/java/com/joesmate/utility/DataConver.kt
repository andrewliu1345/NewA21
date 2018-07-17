package com.joesmate.utility



fun String.toHexByteArray(): ByteArray? {
    var s = this.replace(" ", "").toUpperCase()
    var length = s.length / 2
    val hexChars = s.toCharArray()
    var buffer: ByteArray = ByteArray(length)
    for (i in 0..length - 1) {
        val pos = i * 2

        buffer[i / 2] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
    }
    return buffer
}

private fun charToByte(c: Char): Byte {

    return "0123456789ABCDEF".indexOf(c).toByte()
}

fun ByteArray?.toHexString(length: Int): String? {
    var len = length
    val stringBuilder = StringBuilder("")
    if (this == null || this.size <= 0) {
        return null
    }
    if (this.size < length) {
        len = this.size
    }
    for (i in 0..len - 1) {
        val v = this[i].toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}

fun Int.toByteArrary(): ByteArray {
    var h = this shr (8) and (0xff)
    var l = this and (0xff)
    return byteArrayOf(h.toByte(), l.toByte())

}

fun ByteArray.toIntH(): Int {
    var sum = 0
    if (this == null) {
        return sum
    }

    val ilen = this.size
    for (i in 0 until ilen) {
        sum += ((this[i].toInt() shl 8) * (ilen - 1 - i)) and 0xFFFF
    }
    return sum
}
