package com.joesmate

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.joesmate.server.bt.BTService
import com.jostmate.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent = Intent(this@MainActivity, BTService::class.java)
        startService(intent)
//        var method = this.javaClass.getDeclaredMethod("readIDCard", ByteArray::class.java)
//        method.isAccessible = true
//        method.invoke(this, byteArrayOf(0, 1))
//        var _in = App.getInstance().applicationContext.assets.open("app.config")
//        //var _fis=FileInputStream("app.config")
//        classpro.load(_in)
//        var classname = classpro.getProperty("C0")
//        var cClass = Class.forName(classname)
//        var factory =cClass.newInstance() as FactoryImpl


    }

//    private fun readIDCard(buffer: ByteArray) {
//        print("xxxx")
//    }
}
