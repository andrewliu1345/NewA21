package com.joesmate.voice.ivoice

import android.content.Context

/**
 * @author andrewliu
 * @create 2018/8/6
 * @Describe
 */
interface BaseVoice {


    fun doSpeek(string: String)
    fun setContext(context: Context)
    fun doClose()
}