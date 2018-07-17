package com.joesmate.entity

import android.app.Application

class App : Application() {
    companion object {
        private var mApp: App? = null
        fun getInstance(): App {
            return mApp!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
    }


}