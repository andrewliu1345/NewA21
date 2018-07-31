package com.joesmate.gpio

/**
 * @author andrewliu
 * @create 2018/7/28
 * @Describe
 */
object GpioFactory {
    //gpio 工厂
    fun createBtGpio(): BaseGpio {
        return BTGpio()
    }

    fun createFinanciaModGpio(): BaseGpio {
        return FinanciaModGpio()
    }

    fun createFinanciaModWorkStateGpio(): BaseGpio {
        return FinanciaModWorkStateGpio()
    }

    fun createRs232Gpio(): BaseGpio {
        return RS232Gpio()
    }

}