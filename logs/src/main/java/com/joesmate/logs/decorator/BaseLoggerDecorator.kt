package com.joesmate.logs.decorator

import com.joesmate.logs.LogMsImpl

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
open class BaseLoggerDecorator : LogMsImpl {
    var _Logger: LogMsImpl? = null

    constructor(logger: LogMsImpl) {
        this._Logger = logger
    }

    override fun d(tag: String?, msg: String?, vararg args: Any?) {
        _Logger?.d(tag, msg, args)
    }

    override fun e(tag: String?, msg: String?, vararg args: Any?) {
        _Logger?.e(tag, msg, args)
    }

    override fun e(tag: String?, msg: String?, tr: Throwable?) {
        _Logger?.e(tag, msg, tr)
    }

    override fun i(tag: String?, msg: String?, vararg args: Any?) {
        _Logger?.i(tag, msg, args)
    }

    override fun v(tag: String?, msg: String?, vararg args: Any?) {
        _Logger?.v(tag, msg, args)
    }
}