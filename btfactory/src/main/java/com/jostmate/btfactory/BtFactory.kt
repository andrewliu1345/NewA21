package com.jostmate.btfactory

import android.content.Context
import android.util.Log
import com.jostmate.ibt.BaseBT
import java.util.*

/**
 * @author andrewliu
 * @create 2018/7/23
 * @Describe
 */
object BtFactory {

    fun CreateBT(context: Context): BaseBT {
        try {
            val properties = Properties()
            val _in = context.assets.open("Bt.config")//读取配置文件
            properties.load(_in)
            val classpath = properties.getProperty("btconnet", "")//获取类
            val CClass = Class.forName(classpath)
            val c = CClass.getConstructor(Context::class.java)
            val obj = c.newInstance(context) ?: throw Exception(String.format("%s类不存在", classpath))
            return obj as BaseBT
        } catch (ex: Exception) {
            Log.e("BtFactory", "CreateBT", ex)
            throw ex
        }

    }
}