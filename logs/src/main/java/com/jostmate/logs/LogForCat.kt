package com.jostmate.logs

import android.util.Log

/**
 * @author andrewliu
 * @create 2018/7/17
 * @Describe
 */
class LogForCat : LogMsImpl() {
    override fun d(tag: String, msg: String, vararg args: Any) {
        var m = String.format(msg, args)
        Log.d(tag, m)
    }

    override fun e(tag: String, msg: String, vararg args: Any) {
        var m = String.format(msg, args)
        Log.e(tag, m)
    }

    override fun e(tag: String, msg: String, tr: Throwable) {

        Log.e(tag, msg, tr)
    }

    override fun i(tag: String, msg: String, vararg args: Any) {
        var m = String.format(msg, args)
        Log.i(tag, m)
    }

    override fun v(tag: String, msg: String, vararg args: Any) {
        var m = String.format(msg, args)
        Log.v(tag, m)
    }
}