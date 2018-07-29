package com.joesmate.entity

import android.app.Application
import com.joesmate.logs.LogMsImpl
import com.joesmate.logs.LoggerFactory

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

    override fun onCreate() {
        super.onCreate()
        mApp = this
        _log = LoggerFactory.createLogger(this.applicationContext)
        _log?.i("APP", "Application.onCreate()", null)
    }


}