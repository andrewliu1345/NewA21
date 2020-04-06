package com.joesmate.utility

import vpos.apipackage.SM
import kotlin.experimental.or
import kotlin.experimental.xor

class HMAC {
    companion object {
        fun Encrypt(r1: ByteArray, r2: ByteArray, r3: ByteArray, label_Kay: ByteArray): ByteArray {
            //初始化 opad ipad
            val opad = ByteArray(64)
            opad.forEachIndexed { index, byte -> opad[index] = 0x5c }
            val ipad = ByteArray(64)
            ipad.forEachIndexed { index, byte -> opad[index] = 0x36 }
            var WorkingKey = ByteArray(16)

            var _r1 = ByteArray(64)
            var _r2 = ByteArray(64)
            var _r3 = ByteArray(64)
            var label = ByteArray(64)

            //填充,补0
            System.arraycopy(r1, 0, _r1, 0, r1.size)
            System.arraycopy(r2, 0, _r2, 0, r2.size)
            System.arraycopy(r3, 0, _r3, 0, r3.size)
            System.arraycopy(label_Kay, 0, label, 0, label_Kay.size)

            var Ko = ByteArray(64)
            var Ki = ByteArray(64)
            var Kl = ByteArray(64)
            var Kil = ByteArray(64)

            //异或
            Ko.forEachIndexed { index, byte -> Ko[index] = _r2[index] xor opad[index] }
            Ki.forEachIndexed { index, byte -> Ki[index] = _r2[index] xor ipad[index] }

            //或
            Kl.forEachIndexed { index, byte -> Kl[index] = label[index] or _r1[index] or _r3[index] }
            Kil.forEachIndexed { index, byte -> Kil[index] = Ki[index] or Kl[index] }

            SM.Lib_SM3Init()
            var kilh = ByteArray(64)
            SM.Lib_SM3R(Kil, 64, kilh)

            var kioh1 = ByteArray(64)
            kioh1.forEachIndexed { index, byte -> kioh1[index] = Ko[index] or kilh[index] }
            SM.Lib_SM3R(kioh1, 64, WorkingKey)

            return WorkingKey
        }
    }
}