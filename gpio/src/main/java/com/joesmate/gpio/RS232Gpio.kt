package com.joesmate.gpio

import com.joesmate.gpio.jni.DevctrlJni

/**
 * @author andrewliu
 * @create 2018/7/30
 * @Describe
 */
class RS232Gpio : BaseGpio {
    var devctrlJni: DevctrlJni = DevctrlJni()
    override fun offPower() {
        devctrlJni.uart_toFinancialModule()
    }

    override fun onPower() {
        devctrlJni.uart_to232()
    }

}