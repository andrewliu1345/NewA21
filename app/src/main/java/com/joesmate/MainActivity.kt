package com.joesmate

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.joesmate.entity.App
import com.joesmate.gpio.GpioFactory
import com.joesmate.server.bt.BTService
import com.joesmate.utility.ClsUtils.createBond
import com.joesmate.utility.ClsUtils.setPairingConfirmation
import com.joesmate.utility.ClsUtils.setPin
import com.joesmate.utility.refreshTextView
import com.joesmate.utility.toHexString
import com.jostmate.R
import kotlinx.android.synthetic.main.activity_main.*
import vpos.apipackage.SM
import vpos.apipackage.Sys
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {
    var bt = GpioFactory.createBtGpio()
    var financiaModGpio = GpioFactory.createFinanciaModGpio()//金融模块上下电GPIO
    var RS232Gpio = GpioFactory.createRs232Gpio()//切换金融模块与Rs232串口GPIO 用与升级金融模块
    var EHandwriteGpio = GpioFactory.createEHandwriteGpio()//电磁屏上下电
    var tts = App.instance!!.TTS

    private var myBroadcastReceiver: BluetoothConnectActivityReceiver? = null//配对广播
    private var intentFilter: IntentFilter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(object : Runnable {
            //欢迎语
            override fun run() {
                Thread.sleep(4000)
                App.instance!!.TTS!!.doSpeek("欢迎使用")
            }

        }).start()

        iniDevice()

        var intent = Intent(this@MainActivity, BTService::class.java)
        startService(intent)

        intentFilter = IntentFilter()
        //这里定义接受器监听广播的类型，这里添加相应的广播
        intentFilter!!.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        //实例化接收器
        myBroadcastReceiver = BluetoothConnectActivityReceiver()
        //注册事件，用与实现蓝牙的自动配对
        registerReceiver(myBroadcastReceiver, intentFilter)

//Init.execute()
    }


    fun iniDevice() {
        object : AsyncTask<Void, String, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {

                bt.offPower()
                EHandwriteGpio.offPower()
                financiaModGpio.onPower()
                RS232Gpio.offPower()//断开rs232 切换到金融

                Thread.sleep(2000)
                Sys.Lib_SetComPath("/dev/ttyHSL1")//设置串口号
                var iRet = Sys.Lib_PowerOn()//金融模块上电连接

                publishProgress("打开串口 iRet=$iRet \n")
                Thread.sleep(1000)
                App.instance!!.LogMs!!.i("MainActivity", "打开串口 iRet=$iRet")
                iRet = Sys.Lib_Beep()

                publishProgress("Sys.Lib_Beep iRet=$iRet \n")

                var ver = ByteArray(4)
                var snr = ByteArray(16)
                iRet = Sys.Lib_GetVersion(ver)
                publishProgress("版本号：${ver.toHexString()} \n")
                iRet = Sys.Lib_ReadSN(snr)
                publishProgress("序列号：${String(snr)} \n")
                iRet = SM.Lib_IS8U256AInit();//国密芯片初始化
                publishProgress("国密芯片初始化:iRet=$iRet \n")

                return null
            }

            override fun onPreExecute() {

                tts?.doSpeek("正在初始化\n")
            }

            override fun onPostExecute(result: Void?) {

                super.onPostExecute(result)
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtMsg.refreshTextView(values[0]!!)
                super.onProgressUpdate(*values)
            }
        }.execute()
    }


    class BluetoothConnectActivityReceiver : BroadcastReceiver() {
        var pin = "000000" //此处为你要连接的蓝牙设备的初始密钥000000

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action //得到action

            var btDevice: BluetoothDevice? = null //创建一个蓝牙device对象
            // 从Intent中获取设备对象
            btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            when (action) {
                (BluetoothDevice.ACTION_FOUND) -> {

                    if (btDevice.bondState == BluetoothDevice.BOND_NONE) {
                        try {
                            //通过工具类ClsUtils,调用createBond方法
                            createBond(btDevice.javaClass, btDevice)
                        } catch (e: Exception) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    }
                }
                ("android.bluetooth.device.action.PAIRING_REQUEST") -> {
                    try {
                        //1.确认配对
                        setPairingConfirmation(btDevice.javaClass, btDevice, true)
                        //2.终止有序广播
                        abortBroadcast() //如果没有将广播终止，则会出现一个一闪而过的配对框。
                        //3.调用setPin方法进行配对...
                        val ret = setPin(btDevice.javaClass, btDevice, pin)
                    } catch (e: Exception) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }

                }
            }

        }

    }
}