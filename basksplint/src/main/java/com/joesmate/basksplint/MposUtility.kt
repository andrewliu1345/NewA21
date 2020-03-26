package com.joesmate.basksplint

import com.emv.CoreLogic
import com.joesmate.entity.App
import com.joesmate.logs.LogMsImpl
import vpos.apipackage.Icc
import vpos.apipackage.Mcr
import vpos.apipackage.Picc

class MposUtility {

    companion object {

        private val mLog: LogMsImpl? = App.instance!!.LogMs
        var Slot = -1;//通道
        var CarType: Byte = -1//卡类型
        //找卡
        /**
         * 0为接触
         * 1为非接
         */
        public fun FindCard(type: Int, timeOut: Int): Int {

            val lpAtr = ByteArray(128)
            val cardtype = ByteArray(1)
            val uid = ByteArray(64)
            var start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < timeOut) {
                when (type) {
                    0 -> {
                        if (FindICCard() == 0)
                            return 0
                    }

                    1 -> {

                        var iRet = FindNfcCard()
                        if (iRet >= 0)
                            return iRet

                    }
                    2 -> {
                        var iRet = FindNfcCard()
                        if (FindNfcCard() > 0)
                            return iRet
                        if (FindICCard() == 0)
                            return 0
                    }
                    else -> {
                        return -1
                    }
                }
                Thread.sleep(10)
            }
            return -1
        }

        //打ICCard
        public fun FindICCard(): Int {

            for (j in 0..3) {
                //  var ret = Icc.Lib_IccCheck(j.toByte())
                //   if (ret == 0) {
                val lpAtr = ByteArray(128)
                CoreLogic.iccPowerOn(0, 15, lpAtr)
                for (i in 1..3) {
                    val lpAtr = ByteArray(40)

                    var ret = Icc.Lib_IccOpen(j.toByte(), i.toByte(), lpAtr)
                    if (ret == 0) {
                        Slot = j;
                        mLog!!.i("FindICCard,成功", "$lpAtr")
                        return 0
                    } else {
                        Icc.Lib_IccClose(j.toByte())
                    }
                }
                // }
            }
            return -1
        }

        //找nfccard
        public fun FindNfcCard(): Int {
            val cardtype = ByteArray(3)
            val uid = ByteArray(50)
            var ret = Picc.Lib_PiccOpen()
            if (0 != ret) {
                Picc.Lib_PiccClose()
                return -2
            }
            ret = PiccCheck()
            if (ret > 0) {
                return ret
            }

            Picc.Lib_PiccClose()
            return -1
        }

        private fun PiccCheck(): Int {
            val cardtype = ByteArray(3)
            val uid = ByteArray(50)
            var ret = Picc.Lib_PiccCheck('A'.toByte(), cardtype, uid)
            if (ret == 0) {
                if (cardtype[1].toInt() == 'M'.toInt()) {
                    return cardtype[1].toInt()
                }

                return cardtype[0].toInt()
            }
            ret = Picc.Lib_PiccCheck('B'.toByte(), cardtype, uid)
            if (ret == 0) {
                return 'B'.toInt()
            }
            ret = Picc.Lib_PiccCheck('M'.toByte(), cardtype, uid)
            if (ret == 0) {
                return cardtype[1].toInt()
            }
            return ret
        }

        public fun FindMcr(): Int {
            var ret = -1
            ret = Mcr.Lib_SelectMcr(0.toByte())//选择类型
            ret = Mcr.Lib_McrOpen()//打开磁头
            if (Mcr.Lib_McrCheck() == 0)
                return 0;
            return -1;
        }

        public fun CloseCard(type: Int): Int {//关闭卡
            when (type) {
                0 -> {//关闭IC卡
                    CloseICCard()
                    return 0
                }

                1 -> {//关系nfc卡
                    CloseNfcCard()
                    return 0

                }
                2 -> {//关闭磁头
                    Mcr.Lib_McrClose()
                    return 0;
                }
                else -> {
                    return -1
                }
            }
        }

        private fun CloseICCard() {
            for (j in 0..3) {
                Icc.Lib_IccClose(j.toByte())
            }
        }

        private fun CloseNfcCard() {
            Picc.Lib_PiccClose()
        }
    }
}