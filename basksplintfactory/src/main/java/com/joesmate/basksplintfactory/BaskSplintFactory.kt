package com.joesmate.basksplintfactory

import com.joesmate.entity.App
import com.joesmate.entity.Common
import com.joesmate.entity.Common.objLock
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening
import com.joesmate.utility.toHexString
import java.util.*
import kotlin.experimental.xor

class BaskSplintFactory {

    companion object {
        val TAG = "BaskSplintFactory"
        private val ArryBaseBask = mutableMapOf<String, BaseBaskSplint>()//功能缓存


        //背夹工厂
        fun createBaskSplint(buffer: ByteArray, listening: BtCallBackListening): BaseBaskSplint? {
            var scmd = (buffer[3].toInt() and 0xff).toString(16).toUpperCase() //byteArrayOf(buffer[3]).toHexString()!!.toUpperCase()
            var bs: BaseBaskSplint? = null
            if (!ArryBaseBask.containsKey(scmd)) {//不存在，判断1

//
//                    if (ArryBaseBask.containsKey(scmd)) {//存在，判断2
//                        return ArryBaseBask.get(scmd)
//                    }

                var classname = App.instance!!.properties.getProperty(scmd)
                if (classname == "" || classname == null) {
                    App.instance!!.LogMs?.e(TAG, "${scmd}配置文件未找到相应类")
                    throw  Exception("配置文件未找到相应类")
                }
                var cClass = Class.forName(classname)//反射找到对映的工厂
                var obj = cClass.newInstance()
                if (obj == null) {//未找到对应的类
                    App.instance!!.LogMs?.e(TAG, "未找到相应类${classname}，无法实例化")
                    throw  Exception("未找到相应类${classname}，无法实例化")
                }
                var factory = obj as FactoryImpl//实例化工厂
                bs = factory.createBaskSplint(listening)
                ArryBaseBask.put(scmd, bs!!)

            } else {
                bs = ArryBaseBask.get(scmd)
            }
            return bs
        }
    }
}


