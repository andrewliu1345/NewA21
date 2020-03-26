package com.joesmate.basksplintfactory
import com.joesmate.basksplint.TransferEncrypt
import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

class TransferEncryptFactory:FactoryImpl {
    override fun createBaskSplint(listening: BtCallBackListening): BaseBaskSplint {
        return TransferEncrypt(listening)//创建身份证实体
    }
}