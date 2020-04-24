package com.joesmate

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.BatteryManager
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {


    var bt = GpioFactory.createBtGpio()
    var financiaModGpio = GpioFactory.createFinanciaModGpio()//金融模块上下电GPIO
    var RS232Gpio = GpioFactory.createRs232Gpio()//切换金融模块与Rs232串口GPIO 用与升级金融模块
    var EHandwriteGpio = GpioFactory.createEHandwriteGpio()//电磁屏上下电
    var tts = App.instance!!.TTS
    var batteryManager: BatteryManager? = null
    private var myBroadcastReceiver: BluetoothConnectActivityReceiver? = null//配对广播
    private var intentFilter: IntentFilter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        Thread(Runnable {//欢迎语
            Thread.sleep(4000)
            App.instance!!.TTS!!.doSpeek("欢迎使用")
        }).start()

//        Thread(Runnable {//获取电量
//            while (true) {
//                var battery = batteryManager!!.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
//                if (battery > 15)
//                    SystemClock.sleep(129000)//每两分钟检测一次
//                else {
//                    SystemClock.sleep(5000)//每5秒提醒一次
//                    App.instance!!.TTS!!.doSpeek("电量低，请及时充电")
//                }
//
//
//            }
//        }).start()
        iniDevice()


        //注册事件，用与实现蓝牙的自动配对
        intentFilter = IntentFilter()
        intentFilter!!.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        myBroadcastReceiver = BluetoothConnectActivityReceiver()
        registerReceiver(myBroadcastReceiver, intentFilter)

        //注册电池状态检测
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(BatteryReceiver, filter);

//Init.execute()
    }

    override fun onDestroy() {
        unregisterReceiver(BatteryReceiver);
        financiaModGpio.offPower()
        unregisterReceiver(myBroadcastReceiver)
        super.onDestroy()
        System.exit(0)
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
                Thread.sleep(2000)
                App.instance!!.LogMs!!.i("MainActivity", "打开串口 iRet=$iRet")
                iRet = Sys.Lib_Beep()
                if (iRet != 0) {
                    financiaModGpio.offPower()

                    financiaModGpio.onPower()
                    Thread.sleep(2000)
                    iRet = Sys.Lib_Beep()
                }

                publishProgress("Sys.Lib_Beep iRet=$iRet \n")

                var ver = ByteArray(4)
                var snr = ByteArray(16)
                iRet = Sys.Lib_GetVersion(ver)
                publishProgress("版本号：${ver.toHexString()} \n")
                var btname = BluetoothAdapter.getDefaultAdapter().name
                publishProgress("版本号：${ver.toHexString()} \n")
                iRet = Sys.Lib_ReadSN(snr)
                publishProgress("蓝牙名：${btname} \n")
                iRet = SM.Lib_IS8U256AInit();//国密芯片初始化
                publishProgress("国密芯片初始化:iRet=$iRet \n")

                return null
            }

            override fun onPreExecute() {
                tts?.doSpeek("正在初始化\n")
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                //初始化完成后，启动服务
                var intent = Intent(this@MainActivity, BTService::class.java)
                startService(intent)
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtMsg.refreshTextView(values[0]!!)
                super.onProgressUpdate(*values)
            }
        }.execute()
    }

    // 声明广播接受者对象
    private val BatteryReceiver = object : BroadcastReceiver() {
        var firstTime = System.currentTimeMillis();
        var secTime: Long = 0;
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            val action = intent.action
            if (action == Intent.ACTION_BATTERY_CHANGED) {
                // 得到电池状态：
                // BatteryManager.BATTERY_STATUS_CHARGING：充电状态。
                // BatteryManager.BATTERY_STATUS_DISCHARGING：放电状态。
                // BatteryManager.BATTERY_STATUS_NOT_CHARGING：未充满。
                // BatteryManager.BATTERY_STATUS_FULL：充满电。
                // BatteryManager.BATTERY_STATUS_UNKNOWN：未知状态。
                val status = intent.getIntExtra("status", 0)
                // 得到健康状态：
                // BatteryManager.BATTERY_HEALTH_GOOD：状态良好。
                // BatteryManager.BATTERY_HEALTH_DEAD：电池没有电。
                // BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE：电池电压过高。
                // BatteryManager.BATTERY_HEALTH_OVERHEAT：电池过热。
                // BatteryManager.BATTERY_HEALTH_UNKNOWN：未知状态。
                val health = intent.getIntExtra("health", 0)
                // boolean类型
                val present = intent.getBooleanExtra("present", false)
                // 得到电池剩余容量
                val level = intent.getIntExtra("level", 0)
                // 得到电池最大值。通常为100。
                val scale = intent.getIntExtra("scale", 0)
                // 得到图标ID
                val icon_small = intent.getIntExtra("icon-small", 0)
                // 充电方式：　BatteryManager.BATTERY_PLUGGED_AC：AC充电。　BatteryManager.BATTERY_PLUGGED_USB：USB充电。
                val plugged = intent.getIntExtra("plugged", 0)
                // 得到电池的电压
                val voltage = intent.getIntExtra("voltage", 0)
                // 得到电池的温度,0.1度单位。例如 表示197的时候，意思为19.7度
                val temperature = intent.getIntExtra("temperature", 0)
                // 得到电池的类型
                val technology = intent.getStringExtra("technology")
                // 得到电池状态
                var statusString = ""
                when (status) {
                    BatteryManager.BATTERY_STATUS_UNKNOWN -> statusString = "unknown"
                    BatteryManager.BATTERY_STATUS_CHARGING -> statusString = "charging"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> statusString = "discharging"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> statusString = "not charging"
                    BatteryManager.BATTERY_STATUS_FULL -> statusString = "full"
                }
                //得到电池的寿命状态
                var healthString = ""
                when (health) {
                    BatteryManager.BATTERY_HEALTH_UNKNOWN -> healthString = "unknown"
                    BatteryManager.BATTERY_HEALTH_GOOD -> healthString = "good"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> healthString = "overheat"
                    BatteryManager.BATTERY_HEALTH_DEAD -> healthString = "dead"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> healthString = "voltage"
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> healthString = "unspecified failure"
                }
                //得到充电模式
                var acString = ""
                when (plugged) {
                    BatteryManager.BATTERY_PLUGGED_AC -> acString = "plugged ac"
                    BatteryManager.BATTERY_PLUGGED_USB -> acString = "plugged usb"
                }
                //显示电池信息
//                tvBattery.setText("""
//    电池的状态：$statusString
//    健康值: $healthString
//    电池剩余容量： $level
//    电池的最大值：$scale
//    小图标：$icon_small
//    充电方式：$plugged
//    充电方式: $acString
//    电池的电压：$voltage
//    电池的温度：${temperature.toFloat() * 0.1}
//    电池的类型：$technology
//    """.trimIndent())
                secTime = System.currentTimeMillis()
                if (level < 15 && status == BatteryManager.BATTERY_STATUS_DISCHARGING && secTime - firstTime > 5000) {
                    App.instance!!.TTS!!.doSpeek("电量低，请及时充电")
                    firstTime = secTime
                }
            }
        }
    }


    class BluetoothConnectActivityReceiver : BroadcastReceiver() {
        var pin = "0000" //此处为你要连接的蓝牙设备的初始密钥000000

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action //得到action

            // 从Intent中获取设备对象
            var btDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) //创建一个蓝牙device对象
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