package com.joesmate.basksplint

import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

/**
 * 心跳包
 */
class HeartPack : BaseBaskSplint {
    constructor(listening: BtCallBackListening) : super(listening)

    override fun setData(buffer: ByteArray) {

        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()//功能代码
        when (tag) {
            0x11 -> {
                backSuessData();//返回成功响应

            }
            else-> {
                backErrData(ByteArray(1) { 1 })
            }
        }
    }

}