package com.joesmate.ibasksplint

import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.DataDispose
import com.joesmate.utility.toHexString
import com.joesmate.utility.toIntH


abstract class BaseBaskSplint {
    private var m_CallBackListening: BtCallBackListening
    protected var m_Cmd: ByteArray = ByteArray(2)//模块、功能
    protected var m_buffer: ByteArray = ByteArray(0)//剔除模块功能标志位后的数据
    protected var isCancel = false//是否取消


    constructor(listening: BtCallBackListening) {
        m_CallBackListening = listening//回调
    }

    open fun setData(buffer: ByteArray) {//具体数据处理在实体类中
        m_Cmd[0] = buffer[3]//模块
        m_Cmd[1] = buffer[4]//功能
        var lenby = buffer.copyOfRange(1, 3).toIntH().toInt()//获取数据长度
        m_buffer = buffer.copyOfRange(5, 5 + lenby - 2)//获取数据，剔除模块功能标志位
    }

    protected fun onCancel() {//取消操作
        isCancel = true
    }

    /**
     * 打包数据后返回数据，一个参数的情况下。
     */
    protected fun backData(buffer: ByteArray, lenght: Int) {//返回数据
        val bsendBuffer = DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, buffer, lenght)
        App.instance!!.LogMs!!.i("backData", "bsendBuffer=${bsendBuffer.toHexString()}")
        m_CallBackListening.backData(bsendBuffer)
    }

    /**
     * 返回数据，打包由上层处理
     */
    protected fun backData( buffer: ByteArray) {//返回数据
        App.instance!!.LogMs!!.i("backData", "bsendBuffer=${buffer.toHexString()}")
        m_CallBackListening.backData(buffer)
    }

    protected fun backErrData(errcode: ByteArray) {//返回错误数据
        val bsendBuffer = DataDispose.toPackData(m_Cmd, Common.ERR_CODE, errcode, errcode.size)
        m_CallBackListening.backData(bsendBuffer)
    }

    protected fun backSuessData() {

        val bsendBuffer =  DataDispose.toPackData(m_Cmd, Common.SUCCEE_CODE, ByteArray(0), 0)
        m_CallBackListening.backData(bsendBuffer)
    }
}