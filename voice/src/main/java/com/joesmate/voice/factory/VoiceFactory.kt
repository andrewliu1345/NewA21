package com.joesmate.voice.factory

import android.content.Context
import android.util.Log
import com.joesmate.voice.ivoice.BaseVoice
import java.util.*

/**
 * @author andrewliu
 * @create 2018/8/6
 * @Describe
 */
class VoiceFactory {
    companion object {
        fun createVoice(context: Context): BaseVoice? {
            try {
                val properties = Properties()
                val _in = context.assets.open("tts.config")//读取配置文件
                properties.load(_in)
                val classpath = properties.getProperty("tts", "")//获取类
                val CClass = Class.forName(classpath)
                val c = CClass.getConstructor(Context::class.java)
                val obj = c.newInstance(context)
                        ?: throw Exception(String.format("%s类不存在", classpath))
                return obj as BaseVoice
            } catch (ex: Exception) {
                Log.e("BtFactory", "CreateBT", ex)
                throw ex
            }
        }
    }
}