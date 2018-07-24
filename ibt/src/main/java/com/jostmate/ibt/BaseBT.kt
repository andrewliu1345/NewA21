package com.jostmate.ibt

import android.content.Context

/**
 * @author andrewliu
 * @create 2018/7/23
 * @Describe
 */
interface BaseBT {
    abstract fun setContext(context: Context)

    
    abstract fun openBt(): Int

    
    abstract fun closeBt(): Int

    
    abstract fun readBt(inputBuff: ByteArray): Int

    
    abstract fun writeBt(outputBuff: ByteArray, length: Int): Int

    
    abstract fun getIsConneted(): Boolean
}