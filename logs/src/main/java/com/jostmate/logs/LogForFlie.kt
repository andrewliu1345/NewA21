package com.jostmate.logs

import android.os.Environment
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.sql.Date
import java.text.SimpleDateFormat

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
class LogForFlie : LogMsImpl() {
    private val ERROR = "Error"
    private val DEBUG = "Debug"
    private val INFO = "Info"
    private var VERBOSE = "Verbose"


    override fun d(tag: String?, msg: String?, vararg args: Any?) {
        if (_Level == EnumLevel.ALL || _Level == EnumLevel.DEBUG)
            writeLogInFileSystem("[$DEBUG] $tag:${String.format(msg!!, args)}")
    }

    override fun e(tag: String?, msg: String?, tr: Throwable?) {
        writeLogInFileSystem("[$ERROR] $tag:${String.format(msg!!, tr?.message!!)}")
    }

    override fun e(tag: String?, msg: String?, vararg args: Any?) {
        writeLogInFileSystem("[$ERROR] $tag:${String.format(msg!!, args)}")
    }

    override fun i(tag: String?, msg: String?, vararg args: Any?) {
        if (_Level == EnumLevel.ALL || _Level == EnumLevel.DEBUG || _Level == EnumLevel.INFO)
            writeLogInFileSystem("[$INFO] $tag:${String.format(msg!!, args)}")
    }

    override fun v(tag: String?, msg: String?, vararg args: Any?) {
        writeLogInFileSystem("[$VERBOSE] $tag:${String.format(msg!!, args)}")
    }

    private fun writeLogInFileSystem(content: String) {
        var content = content
        synchronized(LogForFlie::class.java) {
            val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
            val dirPath = "$sdcardPath/LogMg/"
            var file: File? = null
            var randomAccessFile: RandomAccessFile? = null

            val sFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            content = sFormat.format(Date(System.currentTimeMillis())) + "  " + content
            try {
                file = File(dirPath)
                if (!file.exists()) {
                    file.mkdir()
                }

                val filePath = "$dirPath/log.txt"

                file = File(filePath)
                val currSize = file.length()

                randomAccessFile = RandomAccessFile(filePath, "rwd")
                randomAccessFile.seek(currSize)

                val buffer = content.toByteArray()
                randomAccessFile.write(buffer)
                randomAccessFile.write(10)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
}