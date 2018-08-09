package com.joesmate.new21demo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.emv.CoreLogic
import com.joesmate.entity.App
import com.joesmate.logs.LogMsImpl
import kotlinx.android.synthetic.main.activity_main.*
import vpos.apipackage.Icc
import vpos.apipackage.Picc

class PbocActivity : AppCompatActivity() {
    interface MyDelegate {
        fun doReadCard(channel: Byte): Array<String>?
    }

    var Logs: LogMsImpl? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pboc)
        Logs = App.getInstance().LogMs
    }

    fun getReadRecord(v: View) {
        var ReadAsync = Async(2, 15000)
        ReadAsync.execute(object : MyDelegate {
            override fun doReadCard(channel: Byte): Array<String>? {
                return CoreLogic.GetICCTRXDetails(channel, 0, "15")
            }

        })
    }

    fun getDe55(v: View) {
        var ReadAsync = Async(2, 15000)
        ReadAsync.execute(object : MyDelegate {
            override fun doReadCard(channel: Byte): Array<String>? {
                return CoreLogic.GetICCInfo(channel, "A000000333", "ABCDEFGHIJKL".toUpperCase(), "15");
            }

        })
    }

    fun getArqc(v: View) {
        var ReadAsync = Async(2, 15000)
        ReadAsync.execute(object : MyDelegate {
            override fun doReadCard(channel: Byte): Array<String>? {
                return CoreLogic.GetICCArqc(channel, "P012000000010000Q012000000000000R003156S006111202T00201U006102550V000".toUpperCase(), "A000000333", "15")
            }
        })
    }

    fun getReadLoadRecord(v: View) {
        var ReadAsync = Async(2, 15000)
        ReadAsync.execute(object : MyDelegate {
            override fun doReadCard(channel: Byte): Array<String>? {
                return CoreLogic.GetICCLoadDetails(channel, 0, "A000000333", "15")
            }

        })
    }

    inner class Async : AsyncTask<MyDelegate, String, Array<String>> {
        var _type = 2
        var _timeOut = 15000

        constructor(type: Int, timeOut: Int) {
            _type = type
            _timeOut = timeOut
        }

        override fun doInBackground(vararg params: MyDelegate?): Array<String>? {
            var channel = FindCard(_type, _timeOut)
            if (channel == -1) {
                return null
            }
            publishProgress("找到卡，channel=$channel \n")
            var result = params[0]!!.doReadCard(channel.toByte())
            if (result != null && result.size >= 0) {
                return result
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            txtInfo.text = "请刷卡\n"
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            txtInfo.append(values[0])
        }

        override fun onPostExecute(result: Array<String>?) {
            super.onPostExecute(result)

            if (result != null && result.size > 0) {
                txtInfo.append("读卡成功\n")
                for (msg in result) {
                    txtInfo.append(msg)
                }
                txtInfo.append("\n")
            }
        }
    }


    fun FindCard(type: Int, timeOut: Int): Int {

        val lpAtr = ByteArray(128)
        val cardtype = ByteArray(1)
        val uid = ByteArray(64)
        var start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOut) {
            when (type) {
                0 -> {
                    if (FindICCard() == 0)
                        return 0
                }

                1 -> {

                    if (FindNfcCard() == 0)
                        return 1

                }
                2 -> {
                    if (FindICCard() == 0)
                        return 0
                    if (FindNfcCard() == 0)
                        return 1
                }
                else -> {
                    return -1
                }
            }
            Thread.sleep(100)
        }
        return -1
    }

    fun FindICCard(): Int {
        val lpAtr = ByteArray(128)
        var ret = Icc.Lib_IccCheck(0x00.toByte())
        if (ret == 0) {
            ret = Icc.Lib_IccOpen(0x00.toByte(), 0x01.toByte(), lpAtr)

            if (ret == 0) {
                Logs!!.i("FindICCard,成功", "$lpAtr")
                return 0
            }
        }
        return -1
    }

    fun FindNfcCard(): Int {
        val cardtype = ByteArray(1)
        val uid = ByteArray(64)
        Picc.Lib_PiccOpen()
        var ret = Picc.Lib_PiccCheck(0x00.toByte(), cardtype, uid)
        Picc.Lib_PiccClose()
        if (ret == 0) {
            Logs!!.i("FindNfcCard,成功", "$uid")
            return 0
        }
        return -1
    }
}