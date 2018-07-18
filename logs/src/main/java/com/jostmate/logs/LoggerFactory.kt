package com.jostmate.logs

import android.content.Context
import java.util.*
import java.util.concurrent.locks.Lock

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
class LoggerFactory {
    companion object {
        var log: LogMsImpl? = null
        fun CreateLogger(context: Context): LogMsImpl? {
            if (log == null)
                synchronized(this) {
                    if (log == null) {
                        var properties: Properties = Properties()
                        var _in = context.assets.open("app.config")//读取配置文件
                        properties.load(_in)
                        var slevel = properties.getProperty("logger.level")
                        var classname = "com.jostmate.logs.LogFor${properties.getProperty("logger.factory")}"
                        var level = enumValueOf<EnumLevel>(slevel)
                        var cClass = Class.forName(classname)//反射找到对映的类
                        var obj = cClass.newInstance()
                        if (obj == null) {
                            throw Exception("完法找到类")
                        }
                        var factory = obj as LogMsImpl//实例化工厂
                        factory.setLevel(level)
                        log = factory
                    }

                }
            return log
        }
    }
}