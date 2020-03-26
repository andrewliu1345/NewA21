package com.joesmate.basksplintfactory

import com.joesmate.basksplint.HeartPack
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class HeartFactory :FactoryImpl {
    //心跳包卡工厂
    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return HeartPack(listening)//创建身份证实体
    }
}