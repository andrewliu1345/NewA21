package com.joesmate.new21demo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.joesmate.entity.App
import com.joesmate.gpio.GpioFactory
import com.joesmate.utility.DataDispose
import kotlinx.android.synthetic.main.activity_main.*

import java.util.*
import com.joesmate.utility.toHexString
import vpos.apipackage.*
import vpos.util.ByteUtil
import vpos.apipackage.APDU_RESP
import vpos.apipackage.Icc
import vpos.apipackage.APDU_SEND
import vpos.apipackage.Picc


class MainActivity : AppCompatActivity() {
    companion object {
        var isRs232 = false
        var isQuit = false
        var bt = GpioFactory.createBtGpio()
        var financiaModGpio = GpioFactory.createFinanciaModGpio()
        var financiaModWorkStateGpio = GpioFactory.createFinanciaModWorkStateGpio()
        var RS232Gpio = GpioFactory.createRs232Gpio()
    }

    open override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iniDevice()
        getInfo()
    }


    //var financiaModWorkStateGpio = GpioFactory.createFinanciaModWorkStateGpio()
    fun iniDevice() {
        object : AsyncTask<Void, String, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                bt?.offPower()
                financiaModGpio?.onPower()
                RS232Gpio.offPower()//断开rs232 切换到金融
                isRs232 = false
                Thread.sleep(2000)
                var iRet = Sys.Lib_PowerOn()

                publishProgress("Sys.Lib_PowerOn iRet=$iRet \n")
                Thread.sleep(1000)
                App.getInstance().LogMs!!.i("MainActivity", "上电 iRet=$iRet")
                iRet = Sys.Lib_Beep()

                publishProgress("Sys.Lib_Beep iRet=$iRet \n")

                var ver = ByteArray(4)
                var snr = ByteArray(16)
                iRet = Sys.Lib_GetVersion(ver)
                publishProgress("Sys.Lib_GetVersion iRet=$iRet \n")
                iRet = Sys.Lib_ReadSN(snr)
                publishProgress("Sys.Lib_ReadSN iRet=$iRet \n")

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
                txtInfo.append("完成初始.....\n")
                super.onPostExecute(result)

            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values[0])
                super.onProgressUpdate(*values)
            }
        }.execute()

    }

    fun getInfo() {

    }

    fun getIDCard(v: View) {
        object : AsyncTask<Void, Void, ByteArray>() {
            override fun doInBackground(vararg params: Void?): ByteArray {
                var buffer = ByteArray(2321)
                IDCard.Lib_IDCardOpen()
                var iRet = -1
//                if (iRet != 0) {
//
//                    return ByteArray(0)
//                }
                var startTime = System.currentTimeMillis()
                Thread.sleep(2000)

                while (true) {
                    if (System.currentTimeMillis() - startTime > 10000) {
                        IDCard.Lib_IDCardClose()
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
        object : AsyncTask<Void, String, String>() {
            override fun onPreExecute() {
                txtInfo.text = ""
                txtInfo.append("请刷IC卡 \n")
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String {
                var isOk = false
                for (i in 0..3) {//通道号
                    var iRet = Icc.Lib_IccCheck(i.toByte())
                    if (iRet == 0) {
                        publishProgress("正在读卡....\n")
                        for (j in 1..3) {//电压
                            var ATR = ByteArray(40)
                            iRet = Icc.Lib_IccOpen(i.toByte(), j.toByte(), ATR)
                            if (iRet == 0) {
                                val cmd = ByteArray(4)
                                cmd[0] = 0x00            //0-3 cmd
                                cmd[1] = 0xa4.toByte()
                                cmd[2] = 0x04
                                cmd[3] = 0x00
                                val lc: Short = 0x0e
                                val le: Short = 256

                                val sendmsg = "1PAY.SYS.DDF01"
                                var dataIn = sendmsg.toByteArray()

                                val ApduSend = APDU_SEND(cmd, lc, dataIn, le)
                                var ApduResp: APDU_RESP? = null
                                val resp = ByteArray(516)

                                iRet = Icc.Lib_IccCommand(i.toByte(), ApduSend.bytes, resp)
                                if (0 == iRet) {
                                    ApduResp = APDU_RESP(resp)
                                    var strInfo = "读卡成功： ${ByteUtil.bytearrayToHexString(ApduResp.DataOut, ApduResp.LenOut.toInt())}\n" +
                                            " SWA:${ByteUtil.byteToHexString(ApduResp.SWA)} \n" +
                                            "SWB:${ByteUtil.byteToHexString(ApduResp.SWB)}"

                                    Icc.Lib_IccClose(i.toByte())
                                    return strInfo
                                } else {
                                    Icc.Lib_IccClose(i.toByte())
                                    return "APDU 发送失败 \n"
                                }
                            } else {
                                Icc.Lib_IccClose(i.toByte())
                            }


                        }
                        break
                    } else {
                        Icc.Lib_IccClose(i.toByte())
                    }

                }
                return "读卡失败 \n"
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values[0])
                super.onProgressUpdate(*values)
            }

            override fun onPostExecute(result: String?) {
                txtInfo.append(result)
                super.onPostExecute(result)
            }

        }.execute()


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

            override fun onPreExecute() {
                txtInfo.text = ""
                txtInfo.append("请刷磁条卡 \n")
                super.onPreExecute()
            }

            override fun onPostExecute(result: String?) {
                txtInfo.append(result)
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
                    Thread.sleep(1000)
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
                var len = intArrayOf(1024, 0, 0, 0)

                val writeBuf = ByteArray(9)
                val temp = ByteArray(7)
                writeBuf[0] = 126
                writeBuf[1] = 66
                writeBuf[2] = 100
                writeBuf[3] = 0
                writeBuf[4] = 0
                writeBuf[5] = 0
                writeBuf[6] = 1
                writeBuf[7] = 2
                System.arraycopy(writeBuf, 1, temp, 0, 7)
                writeBuf[8] = DataDispose.getCrc(temp)
                //val writeBuf = byteArrayOf(0x7e, 0x53, 0x36, 0x34, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x31, 0x30, 0x32, 0x33, 0x34)
                publishProgress("请按指纹 \n")
                iRet = Fingerprint.Lib_FpCommunication(writeBuf, writeBuf.size, buffer, len, 3000)//4000ms以内
                if (iRet == 0) {
                    sTxt = "获取指纹成功：${buffer.toHexString(len[0])}"
                } else {
                    sTxt = "获取指纹失败"
                }
                Fingerprint.Lib_FpClose()
                return sTxt
            }

            override fun onPreExecute() {
                txtInfo.text = ""

                super.onPreExecute()
            }

            override fun onPostExecute(result: String?) {
                txtInfo.append(result)
                super.onPostExecute(result)
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values[0])
                super.onProgressUpdate(*values)
            }
        }.execute()


    }


    fun switchRs232(v: View) {
        if (!isRs232) {
            RS232Gpio.onPower()
            isRs232 = true

            txtInfo.append("Rs232模式 \n")
        } else {
            RS232Gpio.offPower()
            isRs232 = false

            txtInfo.append("金融模块模式 \n")
        }
    }


    fun openKey(v: View) {
        isQuit = false
        object : AsyncTask<Void, String, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                var ret = Key.Lib_KbFlush()

                while (isQuit === false) {
                    if (Key.Lib_KbCheck() === 0) {
                        ret = Key.Lib_KbGetKey()

                        if (ret === 28) {//取消
                            break
                        } else {
                            publishProgress("按下 ${KeyValue2String(ret)}\n")
                        }
                    } else {
                        Thread.sleep(300)
                    }
                }
                return null
            }

            override fun onPreExecute() {
                txtInfo.setText("请按数字键盘 \n")
                super.onPreExecute()
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.setText(values[0])
                super.onProgressUpdate(*values)
            }

            override fun onPostExecute(result: Void?) {
                txtInfo.setText("已结束")
                super.onPostExecute(result)
            }
        }.execute()
    }

    fun closeKey(v: View) {
        isQuit = true
    }

    fun KeyValue2String(keyValue: Int): String {
        var string = ""

        when (keyValue) {
            48 -> string = "Key0"
            49 -> string = "Key1"
            50 -> string = "Key2"
            51 -> string = "Key3"
            52 -> string = "Key4"
            53 -> string = "Key5"
            54 -> string = "Key6"
            55 -> string = "Key7"
            56 -> string = "Key8"
            57 -> string = "Key9"
            13 -> string = "Enter"
            27 -> string = "CancelClear"
            28 -> string = "Clear"
        }
        return string
    }

    fun clearScreen(v: View) {
        txtInfo.setText("")
    }

    fun getNfc(v: View) {
        object : AsyncTask<Void, String, String>() {
            override fun onPreExecute() {
                txtInfo.text = ""
                txtInfo.append("请放卡 \n")
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String {
                var ret = Picc.Lib_PiccOpen()

                if (0 != ret) {
                    Picc.Lib_PiccClose()
                    return "射频打开失败 \n"
                }
                var cardtype = ByteArray(3)
                var serialNo = ByteArray(50)
                var pwd = ByteArray(20)
                ret = Picc.Lib_PiccCheck('A'.toByte(), cardtype, serialNo)
                if (0 == ret) {
                    publishProgress("找到卡...\n")
                    val cmd = ByteArray(4)
                    cmd[0] = 0x00            //0-3 cmd
                    cmd[1] = 0xa4.toByte()
                    cmd[2] = 0x04
                    cmd[3] = 0x00
                    val lc: Short = 0x0e
                    val le: Short = 256
                    var dataIn = "1PAY.SYS.DDF01".toByteArray()

                    val ApduSend = APDU_SEND(cmd, lc, dataIn, le)
                    var ApduResp: APDU_RESP? = null
                    val resp = ByteArray(516)

                    ret = Picc.Lib_PiccCommand(ApduSend.bytes, resp)

                    if (0 == ret) {
                        ApduResp = APDU_RESP(resp)
                        var strInfo = "${ByteUtil.bytearrayToHexString(ApduResp.DataOut, ApduResp.LenOut.toInt())}\n" +
                                "SWA:${ByteUtil.byteToHexString(ApduResp.SWA)} \n" +
                                "SWB:${ByteUtil.byteToHexString(ApduResp.SWB)}"
                        Picc.Lib_PiccClose()
                        return strInfo

                    } else {
                        Picc.Lib_PiccClose()
                        return "APUD失败"
                    }
                }
                return "没放卡"
            }

            override fun onProgressUpdate(vararg values: String?) {
                txtInfo.append(values[0])
                super.onProgressUpdate(*values)
            }

            override fun onPostExecute(result: String?) {
                txtInfo.append(result)
                super.onPostExecute(result)
            }

        }.execute()
    }

    var isAwaken = true
    fun setWackup(v: View) {
        if (isAwaken) {
            Sys.Lib_PosSleep()//  .offPower()//休眠
            txtInfo.append("金融模块已休眠 \n")
            isAwaken = false
        } else {
            financiaModWorkStateGpio.onPower()//唤醒
            txtInfo.append("金融模块已唤醒 \n")
            isAwaken = true
        }
    }

    fun setRestart(v: View) {
        object : AsyncTask<Void, String, String>() {
            override fun doInBackground(vararg params: Void?): String {
                financiaModGpio?.offPower()
                Thread.sleep(1000)
                financiaModGpio?.onPower()
                return "金融模块重启成功 \n"
            }

            override fun onPreExecute() {
                txtInfo.append("金融模块正在重启 \n")
                super.onPreExecute()
            }

            override fun onPostExecute(result: String?) {
                txtInfo.append(result)
                super.onPostExecute(result)
            }

            override fun onProgressUpdate(vararg values: String?) {
                super.onProgressUpdate(*values)
            }

        }.execute()


    }

}
