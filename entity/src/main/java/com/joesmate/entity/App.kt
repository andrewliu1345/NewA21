package com.joesmate.entity

import android.app.Application
import com.jostmate.logs.LogMsImpl
import com.jostmate.logs.LoggerFactory

class App : Application() {
    companion object {
        private var mApp: App? = null
        fun getInstance(): App {
            return mApp!!
        }
    }

    var LogMs: LogMsImpl? = null
    override fun onCreate() {
        super.onCreate()
        mApp = this
        LogMs = LoggerFactory.CreateLogger(this.applicationContext)
    }


}