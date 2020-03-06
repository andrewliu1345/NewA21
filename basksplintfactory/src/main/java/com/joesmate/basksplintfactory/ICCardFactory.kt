package com.joesmate.basksplintfactory

import com.joesmate.basksplint.ICCardRead
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class ICCardFactory : FactoryImpl {
    //IC卡工厂

    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return ICCardRead(listening)//创建身份证实体
    }
}