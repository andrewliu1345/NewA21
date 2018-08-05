package com.joesmate.gpio

import com.joesmate.gpio.jni.DevctrlJni

/**
 * @author andrewliu
 * @create 2018/7/28
 * @Describe
 */
class FinanciaModWorkStateGpio : BaseGpio {
    var devctrlJni: DevctrlJni = DevctrlJni()
    override fun onPower() {//唤醒
        devctrlJni.FinancialModule_wakeup()
    }

    override fun offPower() {//休眠
        devctrlJni.FinancialModule_sleep()
    }

}