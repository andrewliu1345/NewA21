package com.joesmate.logs.decorator

import android.util.Log
import com.joesmate.logs.LogMsImpl

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
class LogForCatDecorator(logger: LogMsImpl) : BaseLoggerDecorator(logger) {
//    constructor(logger: LogMsImpl) : super(logger) {
//
//    }

    override fun d(tag: String?, msg: String?, vararg args: Any?) {
        var m = String.format(msg!!, args)
        Log.d(tag, m)
        _Logger!!.d(tag, msg, args)
    }

    override fun e(tag: String?, msg: String?, vararg args: Any?) {
        var m = String.format(msg!!, args)
        Log.e(tag, m)
        _Logger!!.e(tag, msg, args)

    }

    override fun e(tag: String?, msg: String?, tr: Throwable?) {

        Log.e(tag, msg, tr)
        _Logger!!.e(tag, msg, tr)
    }

    override fun i(tag: String?, msg: String?, vararg args: Any?) {
        var m = String.format(msg!!, args)
        Log.i(tag, m)
        _Logger!!.i(tag, msg, args)
    }

    override fun v(tag: String?, msg: String?, vararg args: Any?) {
        var m = String.format(msg!!, args)
        Log.v(tag, m)
        _Logger!!.v(tag, msg, args)
    }
}