package com.joesmate.basksplint

import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.*
import vpos.apipackage.SM
import vpos.apipackage.Sys

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
        App.instance!!.LogMs!!.i("Transfer", "publicKey=$publicKey" )
        //获取随机数
        var _c1: ByteArray = SM2.instance.GetRnd()
        var _c3: ByteArray = SM2.instance.GetRnd()
        App.instance!!.LogMs!!.i("Transfer", "c1=${_c1.toHexString()}")
        App.instance!!.LogMs!!.i("Transfer", "c3=${_c3.toHexString()}")
        App.instance!!.LogMs!!.i("Transfer", "dkey=${App.instance!!.devpublickey.toHexString()}")
        //加密
        var cr1 = SM2.instance.Encrypt(_c1, _publickey!!)
        var cr3 = SM2.instance.Encrypt(_c3, _publickey!!)
        var dKey = SM2.instance.Encrypt(App.instance!!.devpublickey, _publickey)


        App.instance!!.LogMs!!.i("Transfer", "cr1=${cr1.toHexString()}")
        App.instance!!.LogMs!!.i("Transfer", "cr3=${cr3.toHexString()}")
        App.instance!!.LogMs!!.i("Transfer", "drKey=${dKey.toHexString()}")


        var buffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, 3, cr1, cr3, dKey)
        backData(buffer)
    }

    //连接第二步 /SM2 解密 R2
    private fun SetR2(buffer: ByteArray) {
        var parms = DataDispose.unPackData(m_buffer, 1)
        if (parms.size > 0) {
            var pCr2 = parms[0]
            var _cr2 = SM2.instance.Decrypt(pCr2, App.instance!!.devprivatekey)
            App.instance!!.LogMs!!.i("SM.SetR2", "cr2=${_cr2.toHexString()}")
            var tmp = ByteArray(_cr2.size - 1)
            System.arraycopy(_cr2, 0, tmp, 0, _cr2.size - 1)
            var crc = DataDispose.getCrc(tmp)
            if (crc == _cr2[_cr2.size - 1]) {
                App.instance!!.cr2 = _cr2
                var key = HMAC.Encrypt(App.instance!!.cr1, App.instance!!.cr2, App.instance!!.cr3, byteArrayOf('K'.toByte(), 'E'.toByte(), 'Y'.toByte()))
                System.arraycopy(key, 0, App.instance!!.workeKey, 0, 16)
                backSuessData()
            } else
                backErrData(ByteArray(1) { 0x02 })
        } else {
            backErrData(ByteArray(1) { 0x01 })
        }
    }

    //连接第三步 /SM3 加密 wKey Hash((r2^opad)||Hash(r2^ipad||(label_key||r1||r3)))
    private fun SendWorkingKey(buffer: ByteArray) {

        var label_key = "key"
        backSuessData()

    }
}