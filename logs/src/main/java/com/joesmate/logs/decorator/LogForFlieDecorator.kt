package com.joesmate.logs.decorator

import android.os.Environment
import com.joesmate.logs.EnumLevel
import com.joesmate.logs.LogMsImpl
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
class LogForFlieDecorator(logger: LogMsImpl) : BaseLoggerDecorator(logger) {
    private val ERROR = "Error"
    private val DEBUG = "Debug"
    private val INFO = "Info"
    private var VERBOSE = "Verbose"

//    constructor(logger: LogMsImpl) : super(logger) {
//
//    }

    override fun d(tag: String?, msg: String?, vararg args: Any?) {
        if (_Level == EnumLevel.ALL || _Level == EnumLevel.DEBUG)
            writeLogInFileSystem("[$DEBUG] $tag:${String.format(msg!!, args)}")
        _Logger!!.d(tag, msg, args)
    }

    override fun e(tag: String?, msg: String?, tr: Throwable?) {
        writeLogInFileSystem("[$ERROR] $tag:${String.format(msg!!, tr?.message!!)}")
        _Logger!!.e(tag, msg, tr)
    }

    override fun e(tag: String?, msg: String?, vararg args: Any?) {
        writeLogInFileSystem("[$ERROR] $tag:${String.format(msg!!, args)}")
        _Logger!!.e(tag, msg, args)
    }

    override fun i(tag: String?, msg: String?, vararg args: Any?) {
        if (_Level == EnumLevel.ALL || _Level == EnumLevel.DEBUG || _Level == EnumLevel.INFO)
            writeLogInFileSystem("[$INFO] $tag:${String.format(msg!!, args)}")
        _Logger!!.i(tag, msg, args)
    }

    override fun v(tag: String?, msg: String?, vararg args: Any?) {
        writeLogInFileSystem("[$VERBOSE] $tag:${String.format(msg!!, args)}")
        _Logger!!.v(tag, msg, args)
    }

    private fun writeLogInFileSystem(mContent: String) {
        var content = mContent
        synchronized(LogForFlieDecorator::class.java) {
            val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
            val dirPath = "$sdcardPath/LogMg/"
            //var file: File? = null
            var randomAccessFile: RandomAccessFile? = null

            val sFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            content = String.format("${sFormat.format(Date(System.currentTimeMillis()))}   ${content}")
            try {
                var file = File(dirPath)
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