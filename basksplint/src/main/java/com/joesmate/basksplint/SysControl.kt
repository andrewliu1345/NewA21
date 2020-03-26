package com.joesmate.basksplint

import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import vpos.apipackage.Sys

class SysControl : BaseBaskSplint {
    constructor(listening: BtCallBackListening) : super(listening)

    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码
        when (tag) {
            1 -> {
                var bSnr = ByteArray(16)
                var iRet = Sys.Lib_ReadSN(bSnr)
                if (iRet == 0) {
                    backData(bSnr, bSnr.size)
                } else {
                    backErrData(ByteArray(1) { 0x01 })
                }
            }
            2 -> {

            }
        }
    }
}