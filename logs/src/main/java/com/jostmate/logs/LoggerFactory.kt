package com.jostmate.logs

import android.content.Context
import com.jostmate.logs.com.jostmate.logs.decorator.LogForCatDecorator
import com.jostmate.logs.com.jostmate.logs.decorator.LogForFlieDecorator
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.logging.Logger

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
class LoggerFactory {
    companion object {
        var log: LogMsImpl? = null
        fun createLogger(context: Context): LogMsImpl? {
            if (log == null)
                synchronized(this) {
                    if (log == null) {
                        var properties: Properties = Properties()
                        var _in = context.assets.open("Logger.config")//读取配置文件
                        properties.load(_in)
                        var slevel = properties.getProperty("logger.level")
                        var type = properties.getProperty("logger.factory")
                        log = Logger()
                        when (type) {
                            "cat" -> {
                                log = LogForCatDecorator(log!!)//控制台Log装饰器
                            }
                            "file" -> {
                                log = LogForFlieDecorator(log!!)//文件Log装饰器
                            }
                            "all" -> {
                                log = LogForCatDecorator(log!!)
                                log = LogForFlieDecorator(log!!)
                            }
                        }
                        val level = enumValueOf<EnumLevel>(slevel)
                        log?.setLevel(level)
                    }

                }
            return log
        }
    }
}