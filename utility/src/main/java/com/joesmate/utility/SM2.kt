package com.joesmate.utility

import vpos.apipackage.Pci
import vpos.apipackage.SM

import kotlin.experimental.xor
import com.joesmate.entity.App


class SM2 {
    private constructor() {
        SM.Lib_SM2nGenKey(App.instance!!.devpublickey, App.instance!!.devprivatekey)//生成设备密钥
        App.instance!!.LogMs!!.i("SM2", "devpublickey=${App.instance!!.devpublickey.toHexString()}")
        App.instance!!.LogMs!!.i("SM2", "devprivatekey=${App.instance!!.devprivatekey.toHexString()}")
    }


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
    fun Encrypt(in_buffer: ByteArray, publicKey: ByteArray): ByteArray {
        var out_buffer = ByteArray(64 + 128 + 32)
        var c1 = ByteArray(64)
        var c2 = ByteArray(128)
        var c3 = ByteArray(32)
        SM.Lib_SM2nEncrypt(in_buffer, in_buffer.size, publicKey, c1, c2, c3)
        var index = 0
        var len = 0

        /****C1***/
        var _c1 = c1.trim()
        len = _c1.size
        System.arraycopy(_c1, 0, out_buffer, index, len)
        if (len % 2 != 0) {
            len++
        }
        index += len
        /****C2***/
        var _c2 = c2.trim()
        len = _c2.size
        System.arraycopy(_c2, 0, out_buffer, index, len)
        if (len % 2 != 0) {
            len++
        }
        index += len
        /****C3***/
        var _c3 = c3.trim()
        len = _c3.size
        System.arraycopy(_c3, 0, out_buffer, index, len)
        if (len % 2 != 0) {
            len++
        }
        index += len

        var out = ByteArray(index)
        System.arraycopy(out_buffer, 0, out, 0, index)
        return out
    }

    /**
     * 解密
     */
    fun Decrypt(in_buffer: ByteArray, privateKey: ByteArray): ByteArray {
        var tmp = ByteArray(128)
        var index = 0
        var C1 = in_buffer.copyOfRange(index, 64)
        index += 64;
        var flag = in_buffer.size - 32;
        var C3 = in_buffer.copyOfRange(flag, in_buffer.size)
        var C2 = in_buffer.copyOfRange(index, flag)
        App.instance!!.LogMs!!.i("SM2.Decrypt", "privateKey=${privateKey.toHexString()}")
        SM.Lib_SM2nDecrypt(C1, C2, C3, C2.size, privateKey, tmp)
        App.instance!!.LogMs!!.i("SM2.Decrypt", "In=${in_buffer.toHexString()}")
        App.instance!!.LogMs!!.i("SM2.Decrypt", "C1=${C1.toHexString()}")
        App.instance!!.LogMs!!.i("SM2.Decrypt", "C2=${C2.toHexString()}")
        App.instance!!.LogMs!!.i("SM2.Decrypt", "C3=${C3.toHexString()}")

//        flag = tmp.findlastnullflag()
//        var out_buffer = ByteArray(flag)
//        System.arraycopy(tmp, 0, out_buffer, 0, flag)
        return tmp.trim()
    }


//    class RetunForEach(message: String?) : Exception(message) {
//
//    }
}