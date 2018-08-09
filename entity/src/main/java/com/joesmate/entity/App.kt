package com.joesmate.entity

import android.app.Application
import com.joesmate.logs.LogMsImpl
import com.joesmate.logs.LoggerFactory
import com.joesmate.voice.factory.VoiceFactory
import com.joesmate.voice.ivoice.BaseVoice

class App : Application() {
    companion object {
        private var mApp: App? = null
        fun getInstance(): App {
            return mApp!!
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
        mApp = this
        _tts = VoiceFactory.createVoice(mApp!!.applicationContext)
        _log = LoggerFactory.createLogger(mApp!!.applicationContext)
        _log?.i("APP", "Application.onCreate()", null)
    }


}