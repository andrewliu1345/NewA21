package com.joesmate.gpio

/**
 * @author andrewliu
 * @create 2018/7/28
 * @Describe
 */
class GpioWrapper : BaseGpio {
    //GPio装饰器
    private var baseGpio: BaseGpio? = null

    constructor(base: BaseGpio) {
        baseGpio = base
    }

    override fun offPower() {
        baseGpio?.offPower()
    }

    override fun onPower() {
        baseGpio?.onPower()
    }
}