package com.joesmate.new21demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.joesmate.signaturepad.views.SignaturePad
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
