package com.joesmate.ibasksplint

import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.entity.Common
import com.joesmate.utility.DataDispose
import com.joesmate.utility.toIntH


abstract class BaseBaskSplint {
    private var m_CallBackListening: BtCallBackListening
    protected var m_Cmd: ByteArray = ByteArray(2)
    protected var m_buffer: ByteArray = ByteArray(0)
    protected var isCancel = false//是否取消


    constructor(listening: BtCallBackListening) {

        m_CallBackListening = listening//回调
    }

    open fun setData(buffer: ByteArray) {//具体数据处理在实体类中
        m_Cmd[0] = buffer[3]//模块
        m_Cmd[1] = buffer[4]//功能
        var lenby = buffer.copyOfRange(1, 3).toIntH().toInt()//获取数据长度
        m_buffer = buffer.copyOfRange(5, 5+ lenby-2)//获取数据，剔除模块功能标志位
    }

    protected fun onCancel() {//取消操作
        isCancel = true
    }

    protected fun backData(buffer: ByteArray?, lenght: Int) {//返回数据
        val bsendBuffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, buffer, lenght)
        m_CallBackListening.backData(bsendBuffer)

    }

    protected fun backErrData(errcode: ByteArray) {//返回错误数据
        val bsendBuffer = DataDispose.toPackData(m_Cmd, Common.ERR_CODE, errcode, errcode.size)
        m_CallBackListening.backData(bsendBuffer)
    }
}