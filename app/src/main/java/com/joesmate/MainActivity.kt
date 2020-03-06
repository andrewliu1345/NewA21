package com.joesmate

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joesmate.entity.App
import com.joesmate.gpio.GpioFactory
import com.joesmate.server.bt.BTService
import com.joesmate.utility.refreshTextView
import com.jostmate.R
import kotlinx.android.synthetic.main.activity_main.*
import vpos.apipackage.Sys


class MainActivity : AppCompatActivity() {
    var bt = GpioFactory.createBtGpio()
    var financiaModGpio = GpioFactory.createFinanciaModGpio()//金融模块上下电GPIO
    var financiaModWorkStateGpio = GpioFactory.createFinanciaModWorkStateGpio()//金融模块休眠GPIO
    var RS232Gpio = GpioFactory.createRs232Gpio()//切换金融模块与Rs232串口GPIO 用与升级金融模块
    var EHandwriteGpio = GpioFactory.createEHandwriteGpio()//电磁屏上下电
    var tts = App.getInstance().TTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(object : Runnable {//欢迎语
            override fun run() {
                Thread.sleep(4000)
                App.getInstance().TTS!!.doSpeek("欢迎使用")
            }

        }).start()

        iniDevice()

        var intent = Intent(this@MainActivity, BTService::class.java)
        startService(intent)

//Init.execute()
    }

    fun iniDevice() {
        object : AsyncTask<Void, String, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {

                bt?.offPower()
                EHandwriteGpio?.offPower()
                financiaModGpio?.onPower()
                RS232Gpio.offPower()//断开rs232 切换到金融

                Thread.sleep(2000)
                Sys.Lib_SetComPath("/dev/ttyHSL1")//设置串口号
                var iRet = Sys.Lib_PowerOn()//金融模块上电连接

                publishProgress("打开串口 iRet=$iRet \n")
                Thread.sleep(1000)
                App.getInstance().LogMs!!.i("MainActivity", "打开串口 iRet=$iRet")
                iRet = Sys.Lib_Beep()

                publishProgress("Sys.Lib_Beep iRet=$iRet \n")

                var ver = ByteArray(4)
                var snr = ByteArray(16)
                iRet = Sys.Lib_GetVersion(ver)
                publishProgress("Sys.Lib_GetVersion iRet=$iRet \n")
                iRet = Sys.Lib_ReadSN(snr)
                publishProgress("Sys.Lib_ReadSN iRet=$iRet \n")

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
}