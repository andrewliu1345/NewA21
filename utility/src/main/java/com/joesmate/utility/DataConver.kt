package com.joesmate.utility

import android.widget.TextView
import java.nio.ByteBuffer


fun String.toHexByteArray(): ByteArray? {
    var s = this.replace(" ", "").toUpperCase()
    var length = s.length / 2
    val hexChars = s.toCharArray()
    var buffer = ByteArray(length)
    for (i in 0 until length) {
        val pos = i * 2

        buffer[i] = ((charToByte(hexChars[pos]).toInt() shl 4) + charToByte(hexChars[pos + 1]).toInt()).toByte()
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

fun ByteArray?.toHexString(): String? {

    val stringBuilder = StringBuilder("")
    if (this == null || this.isEmpty()) {
        return null
    }
    var len = this.size

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


fun ByteArray.toIntH(): Long {
    var sum: Long = 0
    val ilen = this.size
    for (i in 0 until ilen) {
        sum += ((this[i].toLong() and 0xFF) shl (8 * (ilen - 1 - i)))
    }
    return sum
}

fun ByteArray.toIntL(): Long {
    var sum: Long = 0
    val ilen = this.size
    for (i in 0 until ilen) {
        sum += ((this[i].toLong() and 0xFF) shl (8 * i))
    }
    return sum
}

fun TextView.refreshTextView(msg: String) {
    this.append(msg)
    var offset = this.lineCount * this.lineHeight
    if (offset > this.height) {
        this.scrollTo(0, offset - this.height)
    }
}

fun Long.toByteArray(): ByteArray {
    val buffer: ByteBuffer = ByteBuffer.allocate(8)
    buffer.putLong(0, this)
    return buffer.array()
}

fun ByteArray.toLong(): Long {
    val buffer = ByteBuffer.allocate(8)
    buffer.put(this, 0, this.size)
    buffer.flip()
    return buffer.long
}
fun ByteArray.trim(): ByteArray {
    var flag = this.findlastnullflag()
    return this.copyOfRange(0, flag+1)
}
fun ByteArray.findlastnullflag(): Int {
    var flag = this.size - 1

    for (index in flag downTo 0) {
        if (this[index] != 0x00.toByte()) {
            return index
        }
    }

    return flag
}