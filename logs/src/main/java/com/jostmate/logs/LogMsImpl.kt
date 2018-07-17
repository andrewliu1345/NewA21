package com.jostmate.logs


/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
abstract class LogMsImpl {

    protected var _Level = EnumLevel.ALL
    fun setLevel(level: EnumLevel) {
        _Level = level
    }

    /**
     *
     */
    abstract fun v(tag: String, msg: String, vararg args: Any)

    /**
     *
     */
    abstract fun i(tag: String, msg: String, vararg args: Any)

    /**
     *
     */
    abstract fun e(tag: String, msg: String, vararg args: Any)

    /**
     *
     */
    abstract fun e(tag: String, msg: String, tr: Throwable)

    /**
     *
     */
    abstract fun d(tag: String, msg: String, vararg args: Any)
}