package com.josemate.serialportbt

import android.content.Context
import com.joesmate.gpio.GpioFactory
import com.joesmate.utility.GeneralFunction
import com.joesmate.utility.toByteArrary
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
    var btGpio = GpioFactory.createBtGpio()
    internal val LCM = byteArrayOf(0xAA.toByte(), 0x00.toByte(), 0x02.toByte(), 0x52.toByte(), 0x00.toByte(), 0xAC.toByte())
    override fun setName(name: String?) {
        var text = "joesmate"
        try {
            if (text != null && text != "")
                text = name!!
            val cmd = byteArrayOf(0x08.toByte(), 0x01.toByte())
            val NameBuff = text.toByteArray(charset("US-ASCII"))
            val len = NameBuff.size + 1
            if (len > 255)
                return
            val tmp = ByteArray(NameBuff.size)
            val flag = 0


            System.arraycopy(NameBuff, 0, tmp, flag, NameBuff.size)
            val buff = CreBtSendData(cmd, tmp)

            btGpio.offPower()
            Thread.sleep(200)
            btGpio.onPower()
            Thread.sleep(350)
            var iRet = LibSerialPort.device_write(mfd, buff, buff.size)//指令写入
            Thread.sleep(50)
            iRet = LibSerialPort.device_write(mfd, LCM, LCM.size)
            btGpio.offPower()
            //Thread.sleep(200);
            btGpio.onPower()
            //Thread.sleep(3000);


        } catch (ex: Exception) {

            return
        }



        return
    }

    override fun getName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        btGpio.onPower()
        Thread.sleep(350)
        // BtStaDev.getInstance().BtPowerOn();
        mfd = LibSerialPort.device_open(path, baud)
        return if (mfd > 0)
            0
        else
            -1
    }


    override fun closeBt(): Int {
        btGpio.offPower()
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

    private fun CreBtSendData(cmd: ByteArray, data: ByteArray): ByteArray {
        val len = cmd.size + data.size
        val lendata = len.toByteArrary()
        val tmp = ByteArray(len + 2)
        var flag = 0

        System.arraycopy(lendata, 0, tmp, flag, 2)
        flag += 2
        System.arraycopy(cmd, 0, tmp, flag, cmd.size)
        flag += cmd.size
        System.arraycopy(data, 0, tmp, flag, data.size)
        flag += data.size
        var sum = 0
        for (item in tmp) {
            sum += item.toInt() and 0xff
        }
        val crcsum = 0x0100 - (sum and 0x00ff)

        val buff = ByteArray(flag + 2)
        flag = 0
        buff[flag++] = 0xaa.toByte()
        System.arraycopy(tmp, 0, buff, flag, tmp.size)
        flag += tmp.size
        buff[flag++] = crcsum.toByte()
        return buff
    }
}