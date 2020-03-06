package com.joesmate.basksplint


import com.joesmate.entity.App
import com.emv.CoreLogic
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.logs.LogMsImpl
import com.joesmate.utility.toIntH
import vpos.apipackage.Icc
import vpos.apipackage.Picc

class ICCardRead : BaseBaskSplint {
    constructor(listening: BtCallBackListening) : super(listening)

    interface MyDelegate {
        fun doReadCard(channel: Byte, timeOut: Int): Array<String>?
    }
    val mLog: LogMsImpl? = App.getInstance().LogMs
    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码

        // var icType = m_Cmd[2].toInt()//
//        var aidList = m_Cmd[3].toInt()
//        var
        when (tag) {
            0 -> {//读取IC卡客户信息接口函数

            }
            1 -> {//从IC卡获取ARQC接口函数
                var parms = com.joesmate.utility.DataDispose.unPackData(m_buffer, 3)
                var icType = parms[0].toIntH()
                var txtdata=parms[1].toString(Charsets.UTF_8)
                var aidList = parms[2].toString(Charsets.UTF_8)
                var timeout = parms[3].toIntH()//
                ReadICCardFuns(icType.toInt(), timeout.toInt()).toDoExecute(object : MyDelegate {
                    override fun doReadCard(channel: Byte, timeOut: Int): Array<String>? {
                        return CoreLogic.GetICCArqc(channel, txtdata, aidList, timeOut.toString())
                    }
                })
            }
            2 -> {//向IC卡发送ARPC及写卡脚本接口函数

            }
            3 -> {//获取IC卡交易日志
                var parms = com.joesmate.utility.DataDispose.unPackData(m_buffer, 3)
                var icType = parms[0].toIntH()
                var nolog=parms[1].toIntH().toInt()
                var timeout = parms[2].toIntH()//
                ReadICCardFuns(icType.toInt(), timeout.toInt()).toDoExecute(object : MyDelegate {
                    override fun doReadCard(channel: Byte, timeOut: Int): Array<String>? {
                        return CoreLogic.GetICCTRXDetails(channel, nolog, timeOut.toString())
                    }
                })
            }
            4 -> {//读取IC卡圈存明细
                var parms = com.joesmate.utility.DataDispose.unPackData(m_buffer, 3)
                var icType = parms[0].toIntH()
                var nolog=parms[1].toIntH().toInt()
                var aidList = parms[2].toString(Charsets.UTF_8)
                var timeout = parms[3].toIntH()//
                ReadICCardFuns(icType.toInt(), timeout.toInt()).toDoExecute(object : MyDelegate {
                    override fun doReadCard(channel: Byte, timeOut: Int): Array<String>? {
                        return CoreLogic.GetICCLoadDetails(channel, nolog, aidList, timeOut.toString())
                    }
                })
            }
            5 -> {//读取IC卡客户信息及ARQC接口函数

            }
            else -> {
                backErrData(byteArrayOf(0, 1))
            }
        }
    }

    //找卡
    private fun FindCard(type: Int, timeOut: Int): Int {

        val lpAtr = ByteArray(128)
        val cardtype = ByteArray(1)
        val uid = ByteArray(64)
        var start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOut) {
            when (type) {
                0 -> {
                    if (FindICCard() == 0)
                        return 0
                }

                1 -> {

                    if (FindNfcCard() == 0)
                        return 1

                }
                2 -> {
                    if (FindICCard() == 0)
                        return 0
                    if (FindNfcCard() == 0)
                        return 1
                }
                else -> {
                    return -1
                }
            }
            Thread.sleep(100)
        }
        return -1
    }

    //打ICCard
    private fun FindICCard(): Int {

        for (j in 0..3) {
            //  var ret = Icc.Lib_IccCheck(j.toByte())
            //   if (ret == 0) {
            val lpAtr = ByteArray(128)
            CoreLogic.iccPowerOn(0, 15, lpAtr)
            for (i in 1..3) {
                val lpAtr = ByteArray(40)

                var ret = Icc.Lib_IccOpen(j.toByte(), i.toByte(), lpAtr)

                if (ret == 0) {
                    mLog!!.i("FindICCard,成功", "$lpAtr")
                    return 0
                } else {
                    Icc.Lib_IccClose(j.toByte())
                }
            }
            // }
        }
        return -1
    }

    //找nfccard
    private fun FindNfcCard(): Int {
        val cardtype = ByteArray(3)
        val uid = ByteArray(50)
        var ret = Picc.Lib_PiccOpen()
        if (0 != ret) {
            Picc.Lib_PiccClose()
            return -2
        }
        ret = Picc.Lib_PiccCheck('A'.toByte(), cardtype, uid)
        if (ret == 0) {
            mLog!!.i("FindNfcCard,成功", "$uid")
            return 0
        }
        Picc.Lib_PiccClose()
        return -1
    }

    inner class ReadICCardFuns {
        var _type = 2
        var _timeOut = 15000


        constructor(type: Int, timeOut: Int) {
            _type = type
            _timeOut = timeOut

        }

        fun toDoExecute(delegate: MyDelegate) {

            App.getInstance().TTS!!.doSpeek("请放卡")
            var channel = FindCard(_type, _timeOut*1000)//找卡（PICC,IC）
            if (channel == -1) {
                App.getInstance().TTS!!.doSpeek("读卡超时")
                backErrData(ByteArray(1) { 0x03 })//超时
                return
            }

            var result = delegate!!.doReadCard(channel.toByte(), _timeOut)//具体操作
            if (channel == 1) {
                Picc.Lib_PiccClose()
            }
            if (result != null && result.size >= 0) {//读卡成功
                App.getInstance().TTS!!.doSpeek("读卡成功")
                var str = ""
                for (msg in result) {
                    if (msg != null)
                        str += msg
                    else
                        str += "无效数据"
                }
                var byteArray = str.toByteArray(Charsets.UTF_8)
                backData(byteArray, byteArray.size)
            } else {
                backErrData(ByteArray(1) { 0x01 })//返回错误的数据
            }

        }

    }
}