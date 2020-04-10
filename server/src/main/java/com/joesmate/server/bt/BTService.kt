package com.joesmate.server.bt

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.joesmate.basksplintfactory.BaskSplintFactory
import com.joesmate.btfactory.BtFactory
import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.logs.LogMsImpl
import com.joesmate.utility.DataDispose
import com.joesmate.utility.toHexString
import com.josemate.ibt.BaseBT
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * @author andrewliu
 * @create 2018/7/26
 * @Describe
 */
class BTService : Service(), BtCallBackListening {
    //返回数据回调
    override fun backData(buffer: ByteArray) {
        synchronized(Common.backDataLock) {//加锁，避免mbt 资源抢夺
            mbt?.writeBt(buffer, buffer.size)
        }
    }

    //蓝牙服务
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var isClose = false
    val mLog: LogMsImpl? = App.instance!!.LogMs
    private var mbt: BaseBT? = null
    override fun onCreate() {
        super.onCreate()
        mLog?.i("BTService", "服务启动")
        mbt = BtFactory.CreateBT(App.instance!!.applicationContext)//初始化蓝牙
        mbt?.openBt()//打开
        mLog?.i("BTService", "开始蓝牙轮寻")
        if (!mReadSerialPort.isAlive)
            mReadSerialPort.start()//开启蓝牙接收轮寻
    }

    override fun onDestroy() {
        isClose = true
        mReadSerialPort.join()
        mReadSerialPort.interrupt()
        super.onDestroy()
    }

    val threadPool: ExecutorService = Executors.newSingleThreadExecutor()//建立线程池，用与多线程处理数据
    private val mReadSerialPort: Thread = object : Thread() {
        override fun run() {
            // val m_intent = Intent(Common.ACTION_BT_DATA)
            var _in = ByteArray(128)
            var tmp = ByteArray(512)

            while (true) {
                if (isClose) {//关闭后退出
                    return
                }
                if (!mbt!!.getIsConneted()) {
                    sleep(1000)
                    continue
                }
                Arrays.fill(_in, 0x00.toByte())
                Arrays.fill(tmp, 0x00.toByte())
                var tmplen = 0

                var iRet = mbt!!.readBt(_in)//读取蓝牙缓存中的数据

                if (iRet > 0 && _in[0].toInt() == 0x02) {//数据判断头
                    App.instance!!.LogMs!!.i("mReadSerialPort", _in.toHexString(iRet))
                    tmplen = iRet
                    val len = (_in[1].toInt() and (0xff shl 8)) + (_in[2].toInt() and 0xff) + 5
                    System.arraycopy(_in, 0, tmp, 0, iRet)//获取有效数据长度
                    if (tmp[len - 1] != 0x03.toByte() || len > iRet) {//判断数据是否被截断
                        for (count in 0..9) {
                            Arrays.fill(_in, 0x00.toByte())
                            iRet = mbt!!.readBt(_in)
                            System.arraycopy(_in, 0, tmp, tmplen, iRet)
                            tmplen += iRet
                            if (tmp[len - 1] == 0x03.toByte() && len <= tmplen) {//判断数据结尾
                                break
                            }
                        }
                    }


                    threadPool.execute {
                        //填加到线程池中
                        Common.lock.lock()//锁
                        try {
                            var data = ByteArray(len)
                            System.arraycopy(tmp, 0, data, 0, len)
                            var bs = BaskSplintFactory.createBaskSplint(data, this@BTService)//创建相应的工厂
                            bs!!.setData(data)//工厂处理数据
                        } catch (e: Exception) {
                            val bsendBuffer = DataDispose.toPackData(ByteArray(2) { 0x31;0x11 }, Common.ERR_CODE, ByteArray(1) { 1 }, 1)
                            backData(bsendBuffer)
                            e.printStackTrace()
                        } finally {
                            Common.lock.unlock()//释放锁
                        }
                    }
//                    m_intent.putExtra(Common.ACTION_BT_DATA, tmp)
//                    App.instance!!.sendBroadcast(m_intent)


                }
                sleep(200)
            }
        }

    }

}