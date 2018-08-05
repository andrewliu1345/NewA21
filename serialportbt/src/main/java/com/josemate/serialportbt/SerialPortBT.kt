package com.josemate.serialportbt

import android.content.Context
import com.joesmate.gpio.GpioFactory
import com.joesmate.utility.GeneralFunction
import com.josemate.ibt.BaseBT
import com.jostmate.libserialport.LibSerialPort
import com.jostmate.libserialport.LibSerialPort.device_write
import java.util.*

/**
 * @author andrewliu
 * @create 2018/7/23
 * @Describe
 */
class SerialPortBT(private var mContext: Context?) : BaseBT {
    private var path = ""
    private var baud = 115200
    private var mfd = 0
    val BT_PACKAGE = 9728

    init {
        try {
            iniConfig()//加载配置换文件
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    var bt = GpioFactory.createBtGpio()
    private fun iniConfig() {
        bt.onPower()
        val properties = Properties()
        val _in = mContext!!.assets.open("Bt.config")//读取配置文件
        properties.load(_in)
        path = properties.getProperty("path", "")//获取串路径
        baud = Integer.parseInt(properties.getProperty("baudrate", "115200"))//获取波特率
    }

    override fun setContext(context: Context) {
        mContext = context
    }


    override fun openBt(): Int {
        // BtStaDev.getInstance().BtPowerOn();
        mfd = LibSerialPort.device_open(path, baud)
        return if (mfd > 0)
            0
        else
            -1
    }


    override fun closeBt(): Int {
        if (mfd <= 0)
            return -1
        LibSerialPort.device_close(mfd)
        return 0
    }


    override fun readBt(inputBuff: ByteArray): Int {
        return LibSerialPort.device_read_all(mfd, inputBuff)
    }


    override fun writeBt(outputBuff: ByteArray, length: Int): Int {

        if (outputBuff == null || length <= 0)
            return -1
        if (length < BT_PACKAGE) {
            return device_write(mfd, outputBuff, length)
        } else {
            var index: Int
            var len: Int
            var writebuf = ByteArray(BT_PACKAGE)
            index = 0
            while (index < length) {
                len = if (index + BT_PACKAGE < length) BT_PACKAGE else length - index
                System.arraycopy(outputBuff, index, writebuf, 0, len)
                device_write(mfd, writebuf, len)
                GeneralFunction.dalpey(450)
                index += BT_PACKAGE
            }
            return index
        }

//        LibSerialPort.device_write(mfd, outputBuff, length)
//        return 0
    }


    override fun getIsConneted(): Boolean {
        if (mfd > 0)
            return true
        return false
    }
}