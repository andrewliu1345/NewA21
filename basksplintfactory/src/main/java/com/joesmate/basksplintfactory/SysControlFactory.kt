package com.joesmate.basksplintfactory


import com.joesmate.basksplint.SysControl
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class SysControlFactory:FactoryImpl {
    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return SysControl(listening)//创建身份证实体
    }
}