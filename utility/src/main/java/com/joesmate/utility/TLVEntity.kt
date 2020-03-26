package com.joesmate.utility

class TLVEntity {
    var Tag: String = ""
    var Length: Int = 0
    var Value: String = ""
    var SubTLVEntity = mutableListOf<TLVEntity>()
}