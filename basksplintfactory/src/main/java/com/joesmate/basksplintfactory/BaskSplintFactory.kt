package com.joesmate.basksplintfactory

import com.joesmate.entity.App
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.toHexString
import java.util.*

class BaskSplintFactory : FactoryImpl {
    //背夹工厂
    override fun createBaskSplint(buffer: ByteArray, listening: BtCallBackListening): BaseBaskSplint {
        var bcmd: Byte = buffer[3]
        var scmd = byteArrayOf(bcmd).toHexString(1)
        var properties: Properties = Properties()
        var _in = App.getInstance().applicationContext.assets.open("app.config")//读取配置文件
        properties.load(_in)
        var classname = properties.getProperty("C0")
        var cClass = Class.forName(classname)//反射找到对映的工厂
        var factory = cClass.newInstance() as FactoryImpl//实例化工厂
        return factory.createBaskSplint(buffer, listening)//生产出具体类
    }
}


