package com.joesmate.basksplint


import com.joesmate.entity.App
import com.emv.CoreLogic
import com.joesmate.basksplint.MposUtility.Companion.CloseCard
import com.joesmate.basksplint.MposUtility.Companion.FindCard
import com.joesmate.entity.Common
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.logs.LogMsImpl
import com.joesmate.utility.DataDispose
import com.joesmate.utility.TLVPackage
import com.joesmate.utility.toIntH
import vpos.apipackage.*
import java.nio.charset.Charset

class ICCardRead : BaseBaskSplint {
    constructor(listening: BtCallBackListening) : super(listening)

    interface MyDelegate {
        fun doReadCard(channel: Byte, timeOut: Int): Array<String>?
    }

    val mLog: LogMsImpl? = App.instance!!.LogMs
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
                var parms = DataDispose.unPackData(m_buffer, 3)
                var icType = parms[0].toIntH()
                var txtdata = parms[1].toString(Charsets.UTF_8)
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
                var parms = DataDispose.unPackData(m_buffer, 3)
                var icType = parms[0].toIntH()
                var nolog = parms[1].toIntH().toInt()
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
                var nolog = parms[1].toIntH().toInt()
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
            6 -> {//激活IC/NFC 卡
                var parms = DataDispose.unPackData(m_buffer, 2)
                var icType = parms[0].toIntH()
                var timeout = parms[1].toIntH()
                var iRet = FindCard(icType.toInt(), timeout.toInt())//
                if (iRet >= 0)
                    backData(ByteArray(1) { iRet.toByte() }, 1);
                else
                    backErrData(ByteArray(0));
            }
            7 -> {//下电
                var parms = DataDispose.unPackData(m_buffer, 1)
                var icType = parms[0].toIntH()
                var iRet = CloseCard(icType.toInt())
                if (iRet < 0)
                    backErrData(ByteArray(0))
                else
                    backSuessData()
            }
            8 -> {
                var parms = DataDispose.unPackData(m_buffer, 1)
                var dataIn = parms[0]
                val cmd = ByteArray(4)
                cmd[0] = 0x00            //0-3 cmd
                cmd[1] = 0xa4.toByte()
                cmd[2] = 0x04
                cmd[3] = 0x00
                val lc: Short = 0x0e
                val le: Short = 256

                val ApduSend = APDU_SEND(cmd, lc, dataIn, le)
                var ApduResp: APDU_RESP? = null
                val resp = ByteArray(516)

                var iRet = Icc.Lib_IccCommand(MposUtility.Slot.toByte(), ApduSend.bytes, resp)
                if (0 == iRet) {
                    ApduResp = APDU_RESP(resp)
                    backData(ApduResp.dataOut, ApduResp.lenOut.toInt())
                } else
                    backErrData(ByteArray(0));
            }
            9 -> {
                var parms = DataDispose.unPackData(m_buffer, 1)
                var dataIn = parms[0]
                val cmd = ByteArray(4)
                cmd[0] = 0x00            //0-3 cmd
                cmd[1] = 0xa4.toByte()
                cmd[2] = 0x04
                cmd[3] = 0x00
                val lc: Short = 0x0e
                val le: Short = 256

                val ApduSend = APDU_SEND(cmd, lc, dataIn, le)
                var ApduResp: APDU_RESP? = null
                val resp = ByteArray(516)

                var ret = Picc.Lib_PiccCommand(ApduSend.bytes, resp)

                if (0 == ret) {
                    ApduResp = APDU_RESP(resp)
                    backData(ApduResp.dataOut, ApduResp.lenOut.toInt())
                } else
                    backErrData(ByteArray(0));
            }
            0x0A -> {
                var track1 = ByteArray(255)
                var track2 = ByteArray(255)
                var track3 = ByteArray(255)
                var iRet = FindMutltCard(buffer)
                if (iRet >= 0) {
                    when (iRet) {
                        0 -> {//IC
                            var info = CoreLogic.GetICCInfo(0, "A000000333", "KL".toUpperCase(), "15")
                            if (info.isNotEmpty()) {
                                var list = TLVPackage.Construct(info[0])
                                track1 = list[0].Value.toByteArray(Charset.defaultCharset())
                                track2 = list[1].Value.toByteArray(Charset.defaultCharset())
                                track3= ByteArray(0)
                                var buffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, 4, byteArrayOf(iRet.toByte()),track1, track2, track3)
                                backData(buffer)
                            } else {
                                backErrData(ByteArray(0))
                            }
                        }
                        'A'.toInt(),'B'.toInt(),'C'.toInt(),'M'.toInt() -> {//非接
                            var info = CoreLogic.GetICCInfo(1, "A000000333", "KL".toUpperCase(), "15")
                            if (info.isNotEmpty()) {
                                var list = TLVPackage.Construct(info[0])
                                track1 = list[0].Value.toByteArray(Charset.defaultCharset())
                                track2 = list[1].Value.toByteArray(Charset.defaultCharset())
                                track3=ByteArray(0)
                                var buffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, 4, byteArrayOf(iRet.toByte()), track1, track2, track3)
                                backData(buffer)
                            } else {
                                backErrData(ByteArray(0))
                            }
                        }
                       2 -> {//磁条

                            var ret = Mcr.Lib_McrRead(0.toByte(), 0.toByte(), track1, track2, track3)
                            if (ret > 0) {
                                var buffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, 4, byteArrayOf(iRet.toByte()), track1, track2, track3)
                                backData(buffer)
                            } else {
                                backErrData(byteArrayOf(0, 1))
                            }
                        }
                    }
                } else {
                    backErrData(byteArrayOf(0, 1))
                }
            }
            else -> {
                backErrData(byteArrayOf(0, 1))
            }
        }
    }

    fun FindMutltCard(buffer: ByteArray): Int {
        var parms = com.joesmate.utility.DataDispose.unPackData(m_buffer, 2)
        var dwPos = parms[0]
        var timeOut = parms[1].toIntH().toInt()
        var startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeOut * 1000) {


            var _type = MposUtility.FindNfcCard()
            if (_type > 0) {//非接 1
                return _type
            }
            if (MposUtility.FindICCard() >= 0) {//IC 0
                return 0
            }
            if (MposUtility.FindMcr() >= 0) {//磁条 2
                return 2
            }

        }
        return -1
    }

    inner class ReadICCardFuns {
        var _type = 2
        var _timeOut = 15000


        constructor(type: Int, timeOut: Int) {
            _type = type
            _timeOut = timeOut

        }

        fun toDoExecute(delegate: ICCardRead.MyDelegate) {

            App.instance!!.TTS!!.doSpeek("请放卡")
            var channel = FindCard(_type, _timeOut * 1000)//找卡（PICC,IC）
            if (channel == -1) {
                App.instance!!.TTS!!.doSpeek("读卡超时")
                backErrData(ByteArray(1) { 0x03 })//超时
                return
            }

            var result = delegate!!.doReadCard(channel.toByte(), _timeOut)//具体操作
            if (channel == 1) {
                Picc.Lib_PiccClose()
            }
            if (result != null && result.size >= 0) {//读卡成功
                App.instance!!.TTS!!.doSpeek("读卡成功")
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