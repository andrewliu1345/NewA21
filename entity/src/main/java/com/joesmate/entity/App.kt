package com.joesmate.entity

import android.app.Application
import com.joesmate.logs.LogMsImpl
import com.joesmate.logs.LoggerFactory
import com.joesmate.voice.factory.VoiceFactory
import com.joesmate.voice.ivoice.BaseVoice

class App : Application() {
    companion object {
        private var _instance: App? = null
        var instance: App? = null
            get() {
                return _instance!!
            }
    }

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
        _tts = VoiceFactory.createVoice(_instance!!.applicationContext)
        _log = LoggerFactory.createLogger(_instance!!.applicationContext)
        _log?.i("APP", "Application.onCreate()", null)
    }


}