package com.josemate.ibt

import android.content.Context

/**
 * @author andrewliu
 * @create 2018/7/23
 * @Describe
 */
interface BaseBT {
    fun setContext(context: Context)


    fun openBt(): Int


    fun closeBt(): Int


    fun readBt(inputBuff: ByteArray): Int


    fun writeBt(outputBuff: ByteArray, length: Int): Int

    fun getIsConneted(): Boolean

    fun setName(text: String?)

    fun getName():String
}