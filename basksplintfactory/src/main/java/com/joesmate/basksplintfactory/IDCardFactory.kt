package com.joesmate.basksplintfactory

import com.joesmate.basksplint.IDCardRead
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class IDCardFactory : FactoryImpl {
    //身份证工厂
    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return IDCardRead(listening)//创建身份证实体
    }
}