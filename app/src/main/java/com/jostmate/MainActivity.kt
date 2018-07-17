package com.jostmate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import com.joesmate.basksplintfactory.IDCardFactory
import com.joesmate.entity.App
import java.io.FileInputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    var classpro: Properties = Properties()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var _in = App.getInstance().applicationContext.assets.open("app.config")
        //var _fis=FileInputStream("app.config")
        classpro.load(_in)
        var classname = classpro.getProperty("C0")
        var cClass = Class.forName(classname)
        var factory: IDCardFactory =cClass.newInstance() as IDCardFactory

    }
}
