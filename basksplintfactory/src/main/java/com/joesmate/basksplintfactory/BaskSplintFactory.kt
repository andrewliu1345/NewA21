package com.joesmate.basksplintfactory

import com.joesmate.entity.App
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.toHexString
import java.util.*

class BaskSplintFactory : FactoryImpl {
    companion object {
        val TAG = "BaskSplintFactory"
    }

    //背夹工厂
    override fun createBaskSplint(buffer: ByteArray, listening: BtCallBackListening): BaseBaskSplint? {
        var bcmd: Byte = buffer[3]
        var scmd = byteArrayOf(bcmd).toHexString(1)
        var properties: Properties = Properties()
        var _in = App.getInstance().applicationContext.assets.open("app.config")//读取配置文件
        properties.load(_in)
        var classname = properties.getProperty(scmd)
        var cClass = Class.forName(classname)//反射找到对映的工厂
        var obj = cClass.newInstance()
        if (obj == null) {
            App.getInstance().LogMs?.e(TAG, "未找到相应类${classname}，无法实例化")
            throw  Exception("未找到相应类${classname}，无法实例化")
        }
        var factory = obj as FactoryImpl//实例化工厂

        return factory.createBaskSplint(buffer, listening)//生产出具体类
    }
}


