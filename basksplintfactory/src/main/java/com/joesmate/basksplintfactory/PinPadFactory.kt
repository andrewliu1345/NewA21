package com.joesmate.basksplintfactory


import com.joesmate.basksplint.PinPad
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class PinPadFactory: FactoryImpl{
    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return PinPad(listening)//创建身份证实体
    }
}