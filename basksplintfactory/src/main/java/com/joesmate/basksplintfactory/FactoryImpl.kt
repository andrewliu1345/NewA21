package com.joesmate.basksplintfactory

import com.joesmate.ibasksplint.BaseBaskSplint
import com.joesmate.ibtcallback.BtCallBackListening

interface FactoryImpl {
     fun createBaskSplint(buffer: ByteArray, listening: BtCallBackListening): BaseBaskSplint?
}