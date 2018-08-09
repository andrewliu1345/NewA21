package com.joesmate.basksplint

import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

import com.joesmate.utility.DataDispose
import com.joesmate.utility.toIntH

class IDCardRead : BaseBaskSplint {
//    constructor(buffer: ByteArray, listening: BtCallBackListening) : super(buffer, listening) {
//        var tag: Int = m_Cmd[1].toInt()
//        when (tag) {
//            1 -> {
//
//                readIDCard(buffer)
//            }
//            2 -> {
//                getIDInfo(buffer)
//            }
//            else -> {
//                backErrData(byteArrayOf(0, 1))
//            }
//        }
//    }

    constructor(listening: BtCallBackListening) : super(listening)

    override fun setData(buffer: ByteArray) {
        super.setData(buffer)
        var tag: Int = m_Cmd[1].toInt()
        when (tag) {
            1 -> {

                readIDCard(buffer)
            }
            2 -> {
                getIDInfo(buffer)
            }
            else -> {
                backErrData(byteArrayOf(0, 1))
            }
        }
    }

    companion object {
        private var ReadOK = false
    }


    private fun readIDCard(buffer: ByteArray) {
        var m_TimeOut = 0
        val lParams = DataDispose.unPackData(buffer, 1)
        val itimeout = lParams[0].toIntH()
        if (itimeout == 0) {
            m_TimeOut = 30000
        } else {
            m_TimeOut = itimeout * 1000
        }


        ReadOK = false
        backData(null, 0)
        // backData(buffer, buffer.size)
    }

    private fun getIDInfo(buffer: ByteArray) {
        backData(buffer, buffer.size)
    }
}