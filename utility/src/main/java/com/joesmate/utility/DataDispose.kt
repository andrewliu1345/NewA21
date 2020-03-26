package com.joesmate.utility


object DataDispose {

    /**
     * 数据打包
     */
    fun toPackData(cmd: ByteArray, flag: ByteArray, packdata: ByteArray?, length: Int): ByteArray? {
        var blen = length.toByteArrary()
        var tmplen = cmd.size + flag.size + blen.size + length
        var rData = ByteArray(tmplen + 5)
        var tmp = ByteArray(tmplen)
        var index = 0
        System.arraycopy(cmd, 0, tmp, index, cmd.size)
        index += cmd.size
        System.arraycopy(flag, 0, tmp, index, flag.size)
        index += flag.size
        if (length != 0) {
            if (packdata!!.size < length) {
                return null
            }
            System.arraycopy(blen, 0, tmp, index, blen.size)
            index += blen.size
            System.arraycopy(packdata, 0, tmp, index, length)
        }
        var crc = getCrc(tmp)
        rData[0] = 0x02
        rData[1] = (tmplen shr (8)).toByte()
        rData[2] = (tmplen % 0xFF).toByte()
        System.arraycopy(tmp, 0, rData, 3, tmplen)
        rData[tmplen + 3] = crc
        rData[tmplen + 4] = 0x03.toByte()
        return rData
    }

    /**
     * 数据打包
     */
    fun toPackData(cmd: ByteArray, flag: ByteArray, mun: Int, vararg arg: ByteArray?): ByteArray? {
        var len = 0;
        arg.forEach {
            len += it!!.size + 2//数据长度+2位长度
        }
        var tmplen = cmd.size + flag.size + len//计算缓冲区大小
        var buffer = ByteArray(tmplen + 5)
        var tmp = ByteArray(tmplen)
        var index = 0
        System.arraycopy(cmd, 0, tmp, index, cmd.size)
        index += cmd.size
        System.arraycopy(flag, 0, tmp, index, flag.size)
        index += flag.size
        arg.forEach {
            System.arraycopy(it!!.size.toByteArrary(), 0, tmp, index, 2)
            index += 2
            System.arraycopy(it!!, 0, tmp, index, it.size)
            index += it.size
        }

        var crc = getCrc(tmp)
        buffer[0] = 0x02
        buffer[1] = (tmplen shr (8)).toByte()
        buffer[2] = (tmplen ).toByte()
        System.arraycopy(tmp, 0, buffer, 3, tmplen)
        buffer[tmplen + 3] = crc
        buffer[tmplen + 4] = 0x03.toByte()
        return buffer
    }

    /**
     * 数据打包
     */
    fun unPackData(buffer: ByteArray, mun: Int): List<ByteArray> {
        val bBuffLen = ByteArray(2)
        System.arraycopy(buffer, 1, bBuffLen, 0, 2)
        var iBuffler = bBuffLen.toIntH().toInt()

        var index = 0
        val list: MutableList<ByteArray> = mutableListOf()
        for (i in 0 until mun) {
            val bLen = ByteArray(2)
            System.arraycopy(buffer, index, bLen, 0, 2)
            var iLen = bLen.toIntH()
            index += 2

            val bParm = ByteArray(iLen.toInt())
            System.arraycopy(buffer, index, bParm, 0, iLen.toInt())
            index += iLen.toInt()
            list.add(bParm)
            if (index >= iBuffler) {
                break
            }
        }
        return list
    }

    /***
     * 获取CRC 校验
     */
    fun getCrc(data: ByteArray): Byte {
        var temp = 0
        data.forEach {
            temp = temp xor (it.toInt() and 0xff)
        }
        return temp.toByte()
    }

    /**
     * 解析SAMID
     */
    fun SequencSAMID(b: ByteArray?): String {
        var smaid = ""
        var Headcode1Tmp = b!!.copyOfRange(0, 2)
        var Headcode2Tmp = b!!.copyOfRange(2, 4)
        var DatecodeTmp = b!!.copyOfRange(4, 8)
        var HashCode1Tmp = b!!.copyOfRange(8, 12)
        var HashCode2Tmp = b!!.copyOfRange(12, 16)

        var HeadCode1 = Headcode1Tmp.toIntL()
        var HeadCode2 = Headcode2Tmp.toIntL()
        var DateCode = DatecodeTmp.toIntL()
        var HashCode1 = HashCode1Tmp.toIntL()
        var HashCode2 = HashCode2Tmp.toIntL()

        smaid = "${String.format("%02d", HeadCode1)}.${String.format("%02d", HeadCode2)}-${String.format("%08d", DateCode)}-${String.format("%010d", HashCode1)}-${String.format("%010d", HashCode2)}"
        return smaid
    }
}