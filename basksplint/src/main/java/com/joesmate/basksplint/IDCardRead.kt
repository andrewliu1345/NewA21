package com.joesmate.basksplint

import com.joesmate.entity.App
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

import com.joesmate.utility.DataDispose
import com.joesmate.utility.toHexString
import com.joesmate.utility.toIntH
import vpos.apipackage.Fingerprint
import vpos.apipackage.IDCard
import vpos.apipackage.Picc
import java.util.*
import kotlin.concurrent.fixedRateTimer

class IDCardRead : BaseBaskSplint {//身份证模块


    constructor(listening: BtCallBackListening) : super(listening)

    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码
        when (tag) {
            0 -> {//身份证上电
                val lParams = DataDispose.unPackData(m_buffer, 1)
                var type = lParams[0][0].toInt() == 1
                if (type) {
                    OpenIDCard()//打开射频
                } else {
                    closeIDCard()//关闭射频
                }
            }
            1 -> {//读卡

                readIDCard(m_buffer)
            }
            2 -> {//读UID
                getIDCardID()
            }

            else -> {
                backErrData(byteArrayOf(0, 1))
            }
        }
    }

    companion object {
        private var ReadOK = false
    }

    //获取身份证ID
    private fun getIDCardID(): String {
        var cardtype = ByteArray(3)
        var serialNo = ByteArray(50)
        var iRet = Picc.Lib_PiccCheck('A'.toByte(), cardtype, serialNo)//寻找A卡
        if (iRet == 0) {
            backErrData(ByteArray(1) { 0x01 })//非B卡，身份证
            return "非身份证"
        }
        iRet = Picc.Lib_PiccCheck('B'.toByte(), cardtype, serialNo)//寻找B卡
        if (iRet == 0) {//找到B卡后
            var uid = ByteArray(10)
            iRet = Picc.Lib_PiccGetIDNum(uid)//获取UID
            if (iRet == 0) {//获取UID 成功后
                backData(uid, uid.size)//返回获取的UID
            } else {//获取UID 失败
                backErrData(ByteArray(1) { 0x03 })
            }
            return uid.toHexString()!!
        }
        backErrData(ByteArray(1) { 0x02 })//没有任何卡
        return "没有任何卡"

    }

    /**
     * 打开身份证模块
     */
    private fun OpenIDCard() {

        Fingerprint.Lib_SetFgBaudrate(115200)//恢复身份证波特率
        IDCard.Lib_IDCardOpen()//打开身份证模块
        var samid = getSAMID()
        if (samid.isNotEmpty()) {
            backSuessData()
        } else {
            backErrData(ByteArray(1) { 0x01 })
        }
        Thread.sleep(400)
        getSAMID()//检查身份证模块状态

    }

    private fun closeIDCard() {
        var iRet = IDCard.Lib_IDCardClose()
        if (iRet == 0) {
            backSuessData()
        } else {
            backErrData(ByteArray(1) { 0x01 })
        }
    }

    /**
     * 获取身份证信息
     */
    private fun readIDCard(buffer: ByteArray) {
        var retBuffer = ByteArray(2321)//返回数据
        val lParams = DataDispose.unPackData(buffer, 1)
        val itimeout = lParams[0].toIntH()
        Arrays.fill(retBuffer, 0)
        try {
            App.instance!!.TTS!!.doSpeek("请放身份证")
            var iRet = IDCard.Lib_IDCardReadData(retBuffer, 0, itimeout.toInt()) //IDCard.Lib_IDCardRead(idcardinfo, imgdata, 30)
            if (iRet != 0) {
                App.instance!!.TTS!!.doSpeek("读取身份证失败")
                backErrData(ByteArray(1) { 0x01 })//返回错误的数据
                return
            }
        } catch (ex: Exception) {
            App.instance!!.TTS!!.doSpeek("读取身份证失败")
            backErrData(ByteArray(1) { 0x01 })//返回错误的数据
            return
        } finally {
           // IDCard.Lib_IDCardClose()//关闭身份证模块
        }
        App.instance!!.TTS!!.doSpeek("读取身份证成功")
        ReadOK = false
        var tmplen = retBuffer.copyOfRange(0, 2).toIntH().toInt() + 2//获取有效果数据的长度
        var tmpbuffer = retBuffer.copyOfRange(0, tmplen)//拷贝有效数据
        backData(tmpbuffer, tmpbuffer.size)//返回成功后的数据

    }

//    private fun getIDInfo(buffer: ByteArray) {
//        backData(buffer, buffer.size)
//    }

    //获取SAMID
    private fun getSAMID(): String {
        val bytes = ByteArray(20)
        // IDCard.Lib_IDCardOpen()
        var i = 0
        while (i < 10) {
            val r = IDCard.Lib_IDCardReadInfo(bytes)
            if (r == 0)
                break
            i++
            Thread.sleep(200)
        }

        //成功状态
        //  val str = ToolFun.printHexString(bytes, 3)

        // Log.e("成功状态：", str + "=========" + bytes)
        return if ((bytes[1].toInt() == 0x00) and (bytes[2] == 0x90.toByte())) {
            DataDispose.SequencSAMID(bytes.copyOfRange(3, 19))
        } else {
            ""
        }
    }

}