package com.joesmate.utility

import vpos.apipackage.Pci
import kotlin.experimental.xor
import vpos.apipackage.SM

class SM2 {
    private constructor() {
        SM.Lib_SM2nGenKey(devpublickey, devprivatekey)//生成设备密钥
    }

    /**
     * 设备公钥
     */
    var devpublickey = ByteArray(64)

    /**
     * 设备私钥
     */
    var devprivatekey = ByteArray(32)


    companion object {
        val instance: SM2 = SM2()
        fun get(): SM2 {
            return instance
        }

    }

    fun GetRnd(): ByteArray {
        var tmp = ByteArray(16)
        var _c = ByteArray(16)
        var index = 0
        while (index < 15) {
            var _b = ByteArray(8)
            Pci.Lib_PciGetRnd(_b)
            System.arraycopy(_b, 0, tmp, index, 8)
            index += 8
        }
        var _crc: Byte = 0x00
        for (i: Int in 0..14) {
            _crc = _crc xor tmp[i]
        }
        System.arraycopy(tmp, 0, _c, 0, 15)
        _c[15] = _crc
        return _c
    }

    /**
     * 加密
     */
    fun Encrypt(in_buffer: ByteArray, publicKey: ByteArray): MutableList<ByteArray> {
        var out_buffer = mutableListOf<ByteArray>()
        var c1 = ByteArray(64)
        var c2 = ByteArray(128)
        var c3 = ByteArray(32)
        SM.Lib_SM2nEncrypt(in_buffer, in_buffer.size, publicKey, c1, c2, c3)
        var index = 0
        out_buffer.add(c1)
        out_buffer.add(c2.copyOfRange(0, c2.indexOf(0.toByte())))
        out_buffer.add(c3)
//        System.arraycopy(, 0, out_buffer, index, c1.size)
//        index += c1.size
//
//        System.arraycopy(, 0, out_buffer, index, c3.size)
//        index += c3.size
//        var out = ByteArray(index)
//        System.arraycopy(out_buffer, 0, out, 0, index)
        return out_buffer
    }

    /**
     * 解密
     */
    fun Decrypt(in_buffer: ByteArray, privateKey: ByteArray): ByteArray {
        var out_buffer = ByteArray(64 + 16 + 32)
        SM.Lib_SM2nDecrypt(in_buffer.copyOfRange(0, 64), in_buffer.copyOfRange(64, 64 + 16), in_buffer.copyOfRange(64 + 16, 64 + 16 + 32), in_buffer.size, out_buffer, privateKey)
        return out_buffer
    }
}