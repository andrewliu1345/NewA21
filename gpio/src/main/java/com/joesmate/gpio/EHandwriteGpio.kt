package com.joesmate.gpio

import com.joesmate.gpio.jni.DevctrlJni

/**
 * @author andrewliu
 * @create 2018/8/10
 * @Describe
 */
class EHandwriteGpio : BaseGpio {
    var devctrlJni: DevctrlJni = DevctrlJni()
    override fun onPower() {
        devctrlJni.electrscreen_onoff(1)
    }

    override fun offPower() {
        devctrlJni.electrscreen_onoff(0)
    }
}