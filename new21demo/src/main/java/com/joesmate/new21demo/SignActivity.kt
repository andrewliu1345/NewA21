package com.joesmate.new21demo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ThemedSpinnerAdapter
import android.widget.Toast
import com.joesmate.entity.App
import com.joesmate.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {
    internal var isExite = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //timeOutTask.status.
        if (timeOutTask.status != AsyncTask.Status.FINISHED) {
            isExite = true
            Thread.sleep(50)
           // timeOutTask.cancel(true)
        }
        isExite = false
       // timeOutTask.cancel(false)
        timeOutTask.execute(30)

        setContentView(R.layout.activity_sign)
        signature_pad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                Toast.makeText(this@SignActivity, "开始签名", Toast.LENGTH_LONG)
            }

            override fun onSigned() {

            }

            override fun onClear() {

            }

            override fun onGetPaint(x: Float, y: Float, w: Float) {

            }
        })
    }

    override fun onDestroy() {
        if (timeOutTask.status != AsyncTask.Status.FINISHED) {
            isExite = true
            timeOutTask.cancel(true)
        }
        super.onDestroy()
    }

    //    override fun onDestroy() {
//        setResult(1, null)
//        super.onDestroy()
//    }
    private val timeOutTask = object : AsyncTask<Int, String, String>() {
        override fun doInBackground(vararg params: Int?): String {
            val timeout = (params[0]!! * 1000).toLong()
            val startTime = System.currentTimeMillis()
            var msg = ""
            while (true) {
                if (isExite || isCancelled) {
                    msg = "退出"
                    break
                }
                if (System.currentTimeMillis() - startTime > timeout) {
                    msg = "超时退出"
                    break
                }
                Thread.sleep(10)
            }

            return msg
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            App.getInstance().LogMs!!.i("超时计时器","退出")
            finish()
        }
    }

//    inner class Async : AsyncTask<Int, String, String>() {
//        override fun doInBackground(vararg params: Int?): String {
//            val timeout = (params[0]!! * 1000).toLong()
//            val startTime = System.currentTimeMillis()
//            var msg = ""
//            while (true) {
//                if (isExite || isCancelled) {
//                    msg = "退出"
//                    break
//                }
//                if (System.currentTimeMillis() - startTime > timeout) {
//                    msg = "超时退出"
//                    break
//                }
//                Thread.sleep(10)
//            }
//
//            return msg
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            finish()
//        }
//    }

}
