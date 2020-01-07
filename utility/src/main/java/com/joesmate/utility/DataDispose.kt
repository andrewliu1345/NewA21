package com.joesmate.utility


object DataDispose {

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

    fun unPackData(buffer: ByteArray, mun: Int): List<ByteArray> {
        val bBuffLen = ByteArray(2)
        System.arraycopy(buffer, 1, bBuffLen, 0, 2)
        var iBuffler = bBuffLen.toIntH().toInt()

        var index = 5
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

    fun getCrc(data: ByteArray): Byte {
        var temp = 0
        for (item in data) {
            temp = temp xor (item.toInt())
        }
        return temp.toByte()
    }
}