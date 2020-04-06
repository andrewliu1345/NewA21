package com.joesmate.entity

import android.app.Application
import com.joesmate.logs.LogMsImpl
import com.joesmate.logs.LoggerFactory
import com.joesmate.voice.factory.VoiceFactory
import com.joesmate.voice.ivoice.BaseVoice
import java.util.*

class App : Application() {
    companion object {
        private var _instance: App? = null
        var instance: App? = null
            get() {
                return _instance!!
            }
    }

    val properties = Properties()

    var isCancel = false

    /**
     * 设备公钥
     */
    var devpublickey = ByteArray(64)

    /**
     * 设备私钥
     */
    var devprivatekey = ByteArray(32)

    /**
     * C1,C2,C3随机数
     */
    var cr1 = ByteArray(16)
    var cr2 = ByteArray(16)
    var cr3 = ByteArray(16)

    var workeKey = ByteArray(16)

    private var _log: LogMsImpl? = null
    var LogMs: LogMsImpl? = null
        get() {
            return _log
        }

    private var _tts: BaseVoice? = null

    var TTS: BaseVoice? = null
        get() {
            return _tts
        }

    override fun onCreate() {
        super.onCreate()
        _instance = this
        _tts = VoiceFactory.createVoice(this.applicationContext)//语音
        _log = LoggerFactory.createLogger(this.applicationContext)//Log
        _log?.i("APP", "Application.onCreate()")
        val _in = this.applicationContext.assets.open("app.config")//读取配置文件
        properties.load(_in)
    }


}