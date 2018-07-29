package com.joesmate.new21demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.joesmate.entity.App
import com.joesmate.gpio.GpioFactory
import kotlinx.android.synthetic.main.activity_main.*
import vpos.apipackage.IDCard
import vpos.apipackage.Icc
import vpos.apipackage.Sys

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iniDevice()
        getInfo()
    }

    var bt = GpioFactory.createBtGpio()
    var financiaModGpio = GpioFactory.createFinanciaModGpio()
    //var financiaModWorkStateGpio = GpioFactory.createFinanciaModWorkStateGpio()
    fun iniDevice() {
        bt?.onPower()
        financiaModGpio?.onPower()
        var iRet = Sys.Lib_PowerOn()
        txtInfo.append("Sys.Lib_PowerOn iRet=$iRet \n")
        App.getInstance().LogMs!!.i("MainActivity", "上电 iRet=$iRet")
        iRet = Sys.Lib_Beep()
        txtInfo.append("Sys.Lib_Beep iRet=$iRet \n")
    }

    fun getInfo() {
        var ver: ByteArray = ByteArray(4)
        var snr: ByteArray = ByteArray(16)
        var iRet = Sys.Lib_GetVersion(ver)
        txtInfo.append("Sys.Lib_GetVersion iRet=$iRet \n")
        iRet = Sys.Lib_ReadSN(snr)
        txtInfo.append("Sys.Lib_ReadSN iRet=$iRet \n")
        var sVer = ""
        for (v in ver) {
            txtInfo.append("$v")
        }
        txtInfo.append("\n")
        var sSnr = String(snr)
        txtInfo.append(sSnr)
        txtInfo.append("\n")
    }

    fun getIDCard(v: View) {
        IDCard.Lib_IDCardOpen()
        Thread.sleep(2000)
        var idcardinfo: Array<String>? = null
        var imgdata = ByteArray(1024)
        var iRet = IDCard.Lib_IDCardRead(idcardinfo, imgdata)
        txtInfo.append("开始读身份证 \n")
        if (iRet == 0) {
            for (str in idcardinfo!!) {
                txtInfo.append("$str \n")
            }
        } else {
            txtInfo.append("身份证读卡失败 \n")
        }
        IDCard.Lib_IDCardClose()
    }

    fun getICCard(v: View) {//IC卡操做
        var isOk = false
        for (i in 0..3) {//通道号
            for (j in 1..3) {//电压
                var atr = ByteArray(33)
                var iRet = Icc.Lib_IccOpen(i as Byte, j as Byte, atr)
                if (iRet == 0) {
                    txtInfo.append("卡上电成功 \n")
                    isOk = true
                    break
                } else {
                    Icc.Lib_IccClose(i)
                }
            }
        }

    }

    fun doBeep(v: View) {
        Sys.Lib_Beep()
    }

}
