package com.joesmate.basksplint

import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.DataDispose
import com.joesmate.utility.SM2
import com.joesmate.utility.toHexByteArray
import com.joesmate.utility.toHexString
import vpos.apipackage.SM

class TransferEncrypt : BaseBaskSplint {
    //银行私钥
    private var privateKey = "16E532957F1F107F794C1F8157CC768A72BD425B6F425B3C67153DB9082B7F45"

    //银行公钥
    private var publicKey = "E09E5DA835083B694C694027BD4B002DD7E404121171BCCC8632BA0417034A7C5630A05AEDB920EE26661EF42ACBB28741D642872E985DEA0ADEBC4AD1AE47A5"

    constructor(listening: BtCallBackListening) : super(listening)

    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码
        when (tag) {
            5 -> {
                TransferEnInit()
            }
            6 -> {
                SetR2(buffer)

            }
            7 -> {
                SendWorkingKey(buffer)
            }

        }
    }

    //连接第一步 初始化/ sm2加密R1 R3随机数
    private fun TransferEnInit() {

        var _privatekey = privateKey.toHexByteArray()
        var _publickey = publicKey.toHexByteArray()
        var tmp = SM2.instance.Encrypt(SM2.instance.devpublickey, _publickey!!)
        //获取随机数
//        var _c1: ByteArray = SM2.instance.GetRnd()
//        var _c3: ByteArray = SM2.instance.GetRnd()
//        App.instance!!.LogMs!!.i("Transfer","c1=%s",_c1.toHexString())
//        App.instance!!.LogMs!!.i("Transfer","c3=%s",_c3.toHexString())
//        App.instance!!.LogMs!!.i("Transfer","dkey=%s",SM2.instance.devpublickey.toHexString())
//        //加密
//        var cr1 = SM2.instance.Encrypt(_c1, _publickey!!)
//        var cr3 = SM2.instance.Encrypt(_c3, _publickey!!)
//        var dKey = SM2.instance.Encrypt(SM2.instance.devpublickey, _publickey)
//
//
//        App.instance!!.LogMs!!.i("Transfer","cr1=%s",cr1.toHexString())
//        App.instance!!.LogMs!!.i("Transfer","cr3=%s",cr3.toHexString())
//        App.instance!!.LogMs!!.i("Transfer","drKey=%s",dKey.toHexString())


        var buffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, 3, tmp[0], tmp[2], tmp[1])
        backData(buffer)
    }

    //连接第二步 /SM2 解密 R2
    private fun SetR2(buffer: ByteArray) {
        var parms = DataDispose.unPackData(m_buffer, 1)
        if (parms.size > 0) {
            var pCr2 = parms[0]
            var _cr2 = SM2.instance.Decrypt(pCr2, SM2.instance.devprivatekey)
            backSuessData()
        } else {
            backErrData(ByteArray(1) { 0x01 })
        }
    }

    //连接第三步 /SM3 加密 wKey Hash((r2^opad)||Hash(r2^ipad||(label_key||r1||r3)))
    private fun SendWorkingKey(buffer: ByteArray) {

    }
}