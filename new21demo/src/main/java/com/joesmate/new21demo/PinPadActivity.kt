package com.joesmate.new21demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_pin_pad.*
import vpos.apipackage.Sys

class PinPadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_pad)
        //  var list = mutableMapOf<String, Int>("Des" to 0, "3Des" to 1, "SM1" to 2, "SM2" to 3, "SM3" to 4, "SM4" to 5)
        //  var listAdapter = ArrayAdapter<MutableMap<String, Int>>(this, android.R.layout.simple_spinner_item, list)

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(intent)
        Sys.Lib_PowerOff()

    }
}
