package com.joesmate.basksplint

import com.joesmate.entity.Common
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.DataDispose
import com.joesmate.utility.toHexByteArray
import vpos.apipackage.Pci
import vpos.apipackage.SM
import java.security.PublicKey
import kotlin.experimental.or
import kotlin.experimental.xor

class PinPad : BaseBaskSplint {
    constructor(listening: BtCallBackListening) : super(listening)

    //私钥
    private var privateKey = "16E532957F1F107F794C1F8157CC768A72BD425B6F425B3C67153DB9082B7F45"

    //公钥
    private var publicKey = "E09E5DA835083B694C694027BD4B002DD7E404121171BCCC8632BA0417034A7C5630 A05AEDB920EE26661EF42ACBB28741D642872E985DEA0ADEBC4AD1AE47A5"

    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码
        when (tag) {

        }
    }









}