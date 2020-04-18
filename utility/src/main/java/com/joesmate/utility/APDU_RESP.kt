package com.joesmate.utility

class APDU_RESP {
    var LenOut: Short = 0
    var DataOut = ByteArray(512)
    var SWA: Byte = 0
    var SWB: Byte = 0

   constructor(LenOut: Short, DataOut: ByteArray, SWA: Byte, SWB: Byte) {
       this.LenOut = LenOut
       this.DataOut = DataOut
       this.SWA = SWA
       this.SWB = SWB
   }

   constructor(resp: ByteArray) {
       LenOut = (((resp[1] .toInt() shl 8)  and 0xff) + (resp[0].toInt() and 0xff)).toShort()
       System.arraycopy(resp, 2, DataOut, 0, 512)
       SWA = resp[514]
       SWB = resp[515]
   }
}