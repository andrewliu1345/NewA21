package com.joesmate.ibasksplint

import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.entity.Common
import com.joesmate.utility.DataDispose



abstract class BaseBaskSplint {
    private var m_CallBackListening: BtCallBackListening
    protected var m_Cmd: ByteArray = ByteArray(2)

    constructor(buffer: ByteArray, listening: BtCallBackListening) {
        m_Cmd[0] = buffer[3]//模块
        m_Cmd[1] = buffer[4]//功能
        m_CallBackListening = listening//回调
    }

    protected fun backData(buffer: ByteArray?, lenght: Int) {//返回数据
        val bsendbuffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, buffer, lenght)
        m_CallBackListening.backData(bsendbuffer)

    }

    protected fun backErrData(errcode: ByteArray) {//返回错误数据
        m_CallBackListening.backData(errcode)
    }
}