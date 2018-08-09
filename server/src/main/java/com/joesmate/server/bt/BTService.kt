package com.joesmate.server.bt

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.joesmate.basksplintfactory.BaskSplintFactory
import com.josemate.ibt.BaseBT
import com.joesmate.btfactory.BtFactory
import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.logs.LogMsImpl
import java.util.*


/**
 * @author andrewliu
 * @create 2018/7/26
 * @Describe
 */
class BTService : Service(), BtCallBackListening {
    override fun backData(buffer: ByteArray?) {

    }

    //蓝牙服务
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var isClose = false
    val mLog: LogMsImpl? = App.getInstance().LogMs
    private var mbt: BaseBT? = null
    override fun onCreate() {
        super.onCreate()
        mLog?.i("BTService", "服务启动")
        mbt = BtFactory.CreateBT(this.applicationContext)//初始化蓝牙
        mbt?.openBt()//打开
        mLog?.i("BTService", "开始蓝牙轮寻")
        mReadSerialPort.start()//开启蓝牙轮寻
    }

    override fun onDestroy() {
        isClose = true
        super.onDestroy()
    }

    internal var mReadSerialPort: Thread = object : Thread() {
        override fun run() {
            val m_intent = Intent(Common.ACTION_BT_DATA)
            var _in = ByteArray(2048)
            var tmp = ByteArray(4096)
            while (true) {
                if (isClose) {
                    return
                }
                if (!mbt!!.getIsConneted()) {
                    Thread.sleep(1000)
                    continue
                }
                Arrays.fill(_in, 0x00.toByte())
                Arrays.fill(tmp, 0x00.toByte())
                var tmplen = 0

                var iRet = mbt!!.readBt(_in)
                Thread.sleep(8)
                if (iRet > 0 && _in[0].toInt() == 0x02) {
                    tmplen = iRet
                    val len = (_in[1].toInt() and (0xff shl 8)) + (_in[2].toInt() and 0xff) + 5
                    System.arraycopy(_in, 0, tmp, 0, iRet)
                    if (tmp[len - 1] != 0x03.toByte() || len > iRet) {
                        for (count in 0..9) {
                            Arrays.fill(_in, 0x00.toByte())
                            iRet = mbt!!.readBt(_in)
                            System.arraycopy(_in, 0, tmp, tmplen, iRet)
                            tmplen += iRet
                            if (tmp[len - 1] == 0x03.toByte() && len <= tmplen) {
                                break
                            }
                        }
                    }
                    var bs = BaskSplintFactory.createBaskSplint(tmp, this@BTService)
                    bs!!.setData(tmp)
//                    m_intent.putExtra(Common.ACTION_BT_DATA, tmp)
//                    App.getInstance().sendBroadcast(m_intent)


                }

                Thread.sleep(16)
            }
        }

    }

}