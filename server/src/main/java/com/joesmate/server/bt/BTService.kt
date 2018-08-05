package com.joesmate.server.bt

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.josemate.ibt.BaseBT
import com.joesmate.btfactory.BtFactory
import com.joesmate.logs.LogMsImpl

/**
 * @author andrewliu
 * @create 2018/7/26
 * @Describe
 */
class BTService : Service() {
    //蓝牙服务
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val mLog: LogMsImpl? = com.joesmate.entity.App.getInstance().LogMs
    private var mbt: BaseBT? = null
    override fun onCreate() {
        super.onCreate()
        mLog?.i("BTService", "服务启动")
        mbt = BtFactory.CreateBT(this.applicationContext)
        mbt?.openBt()
    }
}