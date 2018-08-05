package com.joesmate

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joesmate.server.bt.BTService
import com.jostmate.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var intent = Intent(this@MainActivity, BTService::class.java)
        startService(intent)
//Init.execute()
    }

//    private var Init = object : AsyncTask<Void, String, String>() {
//        override fun doInBackground(vararg params: Void?): String {
//            // GpioFactory.createBtGpio()?.offPower()
//            return "初始化设备成功 \n"
//        }
//
//        override fun onPreExecute() {
//            txtMsg.append("正在初始化设备,请稍候... \n")
//            super.onPreExecute()
//        }
//
//        override fun onProgressUpdate(vararg values: String?) {
//            super.onProgressUpdate(*values)
//        }
//
//        override fun onPostExecute(result: String?) {
//            txtMsg.append(result)
//            super.onPostExecute(result)
//        }
//    }
}
