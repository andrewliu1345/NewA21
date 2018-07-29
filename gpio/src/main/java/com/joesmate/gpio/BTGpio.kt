package com.joesmate.gpio

import com.example.wwzl.libserialport.DevctrlJni

/**
 * @author andrewliu
 * @create 2018/7/28
 * @Describe
 */
class BTGpio : BaseGpio {
    var devctrlJni: DevctrlJni = DevctrlJni()
    override fun offPower() {
        devctrlJni.bluetooth_onoff(0)
    }

    override fun onPower() {
        devctrlJni.bluetooth_onoff(1)
    }
}