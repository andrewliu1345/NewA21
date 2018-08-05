package com.joesmate.gpio

import com.joesmate.gpio.jni.DevctrlJni

/**
 * @author andrewliu
 * @create 2018/7/28
 * @Describe
 */
class FinanciaModGpio : BaseGpio {//金融模块
    var devctrlJni: DevctrlJni = DevctrlJni()
    override fun onPower() {//打开电源
        devctrlJni.FinancialModule_onoff(1)
    }

    override fun offPower() {//关闭电源
        devctrlJni.FinancialModule_onoff(0)
    }
}