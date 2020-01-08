package com.joesmate.utility

import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater

/**
 * @author andrewliu
 * @create 2018/7/24
 * @Describe
 */
object GeneralFunction {
    internal val TAG = GeneralFunction::class.java.name

    fun compress(inputByte: ByteArray): ByteArray {
        var len: Int
        val defl = Deflater(Deflater.BEST_COMPRESSION, false)
        defl.setStrategy(Deflater.FILTERED)
        defl.setInput(inputByte)
        defl.finish()
        val bos = ByteArrayOutputStream()
        val outputByte = ByteArray(1024)
        try {
            while (!defl.finished()) {
                // 压缩并将压缩后的内容输出到字节输出流bos中
                len = defl.deflate(outputByte)
                bos.write(outputByte, 0, len)
            }
            defl.end()
        } finally {
            bos.close()
        }
        return bos.toByteArray()
    }

    fun dalpey(time: Long) {
        try {
            Thread.sleep(time)
        } catch (ex: Exception) {
            Log.e(TAG, "Daley: ", ex)
        }

    }

    /**
     * 唤醒屏幕
     */
    fun screenOn(context: Context) {
        var pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var method = pm.javaClass.getMethod("wakeUp", Long::class.java)
        method.invoke(pm, SystemClock.uptimeMillis())
        android.util.Log.i("cxq", "screenOn")

    }

    /**
     * 熄灭屏幕
     */
    fun screenOff(context: Context) {
        var pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var method = pm.javaClass.getMethod("goToSleep", Long::class.java)
        method.invoke(pm, SystemClock.uptimeMillis())
        android.util.Log.i("cxq", "screenOff")
    }
}