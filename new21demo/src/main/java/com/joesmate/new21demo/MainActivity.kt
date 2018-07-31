package com.joesmate.new21demo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.joesmate.entity.App
import com.joesmate.gpio.FinanciaModGpio
import com.joesmate.gpio.GpioFactory
import com.joesmate.utility.DataDispose
import kotlinx.android.synthetic.main.activity_main.*
import vpos.apipackage.Fingerprint
import vpos.apipackage.IDCard
import vpos.apipackage.Icc
import vpos.apipackage.Sys

import java.util.*
import vpos.apipackage.Mcr
import com.joesmate.utility.toHexString


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iniDevice()
        getInfo()
    }

    var bt = GpioFactory.createBtGpio()
    var financiaModGpio = GpioFactory.createFinanciaModGpio()
    var RS232Gpio = GpioFactory.createRs232Gpio()
    //var financiaModWorkStateGpio = GpioFactory.createFinanciaModWorkStateGpio()
    fun iniDevice() {
        object : AsyncTask<Void, String, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                bt?.onPower()
                financiaModGpio?.onPower()
                RS232Gpio.offPower()//断开rs232 切换到金融
                Thread.sleep(1000)
                var iRet = Sys.Lib_PowerOn()

                publishProgress("Sys.Lib_PowerOn iRet=$iRet \n")

                App.getInstance().LogMs!!.i("MainActivity", "上电 iRet=$iRet")
                iRet = Sys.Lib_Beep()

                publishProgress("Sys.Lib_Beep iRet=$iRet \n")

                var ver = ByteArray(4)
                var snr = ByteArray(16)
                iRet = Sys.Lib_GetVersion(ver)
                publishProgress("Sys.Lib_GetVersion iRet=$iRet \n")
                iRet = Sys.Lib_ReadSN(snr)
                publishProgress("Sys.Lib_ReadSN iRet=$iRet \n")
                var sVer = ""
                publishProgress("Ver:")
                for (v in ver) {
                    publishProgress("$v")
                }
                publishProgress("\n")
                var sSnr = String(snr)
                publishProgress(sSnr)
                publishProgress("\n")
                return null
            }

            override fun onPostExecute(result: Void?) {
                txtInfo.append("完成初始.....")
                super.onPostExecute(result)

            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values.toString())
                super.onProgressUpdate(*values)
            }
        }

    }

    fun getInfo() {

    }

    fun getIDCard(v: View) {
        object : AsyncTask<Void, Void, ByteArray>() {
            override fun doInBackground(vararg params: Void?): ByteArray {
                var buffer = ByteArray(2321)
                var iRet = -1
                IDCard.Lib_IDCardOpen()
//                if (iRet != 0) {
//
//                    return ByteArray(0)
//                }
                var startTime = System.currentTimeMillis()
                Thread.sleep(2000)

                while (true) {
                    if (System.currentTimeMillis() - startTime > 10000) {

                        return ByteArray(1)
                    }

                    Arrays.fill(buffer, 0)

                    var infoarray = Array<String>(20, { "" })
                    var imgbuff = ByteArray(1024)
                    try {
                        // iRet = IDCard.Lib_IDCardRead(infoarray, imgbuff, 10)
                        iRet = IDCard.Lib_IDCardReadData(buffer, 0, 10) //IDCard.Lib_IDCardRead(idcardinfo, imgdata, 30)
                        if (iRet == 0) {
//                            var xx = infoarray!![0].toByteArray(Charsets.UTF_8)
//                            System.arraycopy(xx!!, 0, buffer, 0, xx?.size)
                            break
                        } else {
                            //return ByteArray(2)
                        }
                    } catch (ex: Exception) {
                        App.getInstance().LogMs!!.e("身份证", "读身份证失败", ex)
                    }

                    Thread.sleep(10)
                }

                IDCard.Lib_IDCardClose()
                return buffer
            }

            override fun onPreExecute() {
                txtInfo.append("开始读卡 \n")
            }

            override fun onPostExecute(result: ByteArray?) {

                //txtInfo.append("身份证打开错误 iRet=$iRet")
                if (result?.size == 0) {
                    txtInfo.append("身份证打开错误 \n")
                }
                if (result?.size == 1) {
                    txtInfo.append("身份证读卡超时 \n")
                }
                if (result?.size == 2) {
                    txtInfo.append("身份证读卡失败 \n")
                }
                if (result?.size as Int > 14) {
                    var bname = ByteArray(30)
                    System.arraycopy(result, 0, bname, 0, 30)
                    var sname = bname.toString(Charsets.UTF_16LE)
                    txtInfo.append("读卡成功 name=$sname \n")
                }
                super.onPostExecute(result)
            }
        }.execute()


    }

    fun getICCard(v: View) {//IC卡操做
        var isOk = false
        for (i in 0..3) {//通道号
            for (j in 1..3) {//电压
                var atr = ByteArray(33)
                var iRet = Icc.Lib_IccOpen(i.toByte(), j.toByte(), atr)
                if (iRet == 0) {
                    txtInfo.append("卡上电成功 \n")
                    isOk = true
                    break
                } else {
                    Icc.Lib_IccClose(i.toByte())
                }
            }
        }

    }

    fun getMagnetic(v: View) {
        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String {
                var sReturn = ""
                var ret = -1
                ret = Mcr.Lib_SelectMcr(0.toByte())//选择类型
                ret = Mcr.Lib_McrOpen()//打开磁头
                if (0 != ret) {
                    App.getInstance().LogMs?.d("MSR_Thread[ run ]", "Lib_MsrOpen error!")
                    sReturn = "Lib_MsrOpen error!"
                }
                var startTime = System.currentTimeMillis()
                while (Mcr.Lib_McrCheck() != 0) {
                    try {
                        if (System.currentTimeMillis() - startTime > 100000) {
                            Mcr.Lib_McrClose()
                            return "刷卡超时"

                        }
                        Thread.sleep(100)
                        //App.getInstance().LogMs?.d("MSR_Thread[ run ]", "Lib_McrCheck...")
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
                var track1 = ByteArray(250)
                var track2 = ByteArray(250)
                var track3 = ByteArray(250)
                ret = Mcr.Lib_McrRead(0.toByte(), 0.toByte(), track1, track2, track3)
                if (ret > 0) {
                    if (ret <= 7) {
                        if (ret and 0x01 === 0x01)
                            sReturn = "track1: ${String(track1).trim { it <= ' ' }}\n"
                        if (ret and 0x02 === 0x02)
                            sReturn += "track2: ${String(track2).trim { it <= ' ' }}\n"
                        if (ret and 0x04 === 0x04)
                            sReturn += "track3: ${String(track3).trim { it <= ' ' }}\n"
                    } else {
                        sReturn = "Lib_MsrRead check data error"
                    }
                }
                return sReturn
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }
        }.execute()
    }

    fun doBeep(v: View) {
        Sys.Lib_Beep()
    }

    fun setSnr(v: View) {
        var sSnr = txtSnr.text.toString()
        var tmp = sSnr.toByteArray(Charsets.UTF_8)
        var bSnr = ByteArray(16)
        System.arraycopy(tmp, 0, bSnr, 0, tmp.size)
        var iRet = Sys.Lib_WriteSN(bSnr)
        txtInfo.append("Sys.Lib_WriteSN sSnr=$sSnr iRet=$iRet \n")
    }

    fun getSnr(v: View) {
        var bSnr = ByteArray(16)
        var iRet = Sys.Lib_ReadSN(bSnr)
        if (iRet == 0) {
            var sSnr = bSnr.toString(Charsets.UTF_8)
            txtSnr.setText(sSnr)
        } else {
            txtInfo.append("序列号写入失败，iRet=$iRet \n")
        }
    }

    fun getFp(v: View) {
        object : AsyncTask<Void, String, String>() {
            override fun doInBackground(vararg params: Void?): String {
                /// var startTime = System.currentTimeMillis()
                var sTxt = ""
                var iRet = Fingerprint.Lib_FpOpen()
                if (iRet == 0) {
                    sTxt = "指纹上电成功 \n"
                    publishProgress(sTxt)

                } else {
                    sTxt = "指纹上电失败 iRet=$iRet \n"
                    Fingerprint.Lib_FpClose()
                    return sTxt
                }
                iRet = Fingerprint.Lib_SetFgBaudrate(9600)
                if (iRet == 0) {
                    sTxt = "指纹波特率设置成功 \n"
                    publishProgress(sTxt)
                } else {
                    sTxt = "指纹上电失败 iRet=$iRet \n"
                    Fingerprint.Lib_FpClose()
                    return sTxt
                }

                var buffer = ByteArray(1024)
                var len = intArrayOf(0, 0, 0, 0)

                val writeBuf = ByteArray(12)
                val temp = ByteArray(10)
                writeBuf[0] = 126
                writeBuf[1] = 66
                writeBuf[2] = -128
                writeBuf[3] = 0
                writeBuf[4] = 0
                writeBuf[5] = 0
                writeBuf[6] = 4
                writeBuf[7] = 0
                writeBuf[8] = 0
                writeBuf[9] = 1
                writeBuf[10] = 0
                System.arraycopy(writeBuf, 1, temp, 0, 10)
                writeBuf[11] = DataDispose.getCrc(temp)

                iRet = Fingerprint.Lib_FpCommunication(writeBuf, writeBuf.size, buffer, len, 10)
                if (iRet == 0) {
                    sTxt = "获取指纹成功：${buffer.toHexString(len[0])}"
                } else {
                    sTxt = "获取指纹失败"
                }
                Fingerprint.Lib_FpClose()
                return sTxt
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values.toString())
                super.onProgressUpdate(*values)
            }
        }.execute()


    }

    fun switchRs232(v: View) {
        RS232Gpio.onPower()
        financiaModGpio?.offPower()
        Thread.sleep(1000)
        financiaModGpio?.onPower()
    }
}
