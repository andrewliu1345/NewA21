package com.joesmate.gpio

import com.example.wwzl.libserialport.DevctrlJni

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