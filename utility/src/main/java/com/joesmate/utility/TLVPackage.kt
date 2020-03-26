package com.joesmate.utility

class TLVPackage {
    companion object {

        /**
         * TLV解析
         */
        fun Construct(buffer: String): MutableList<TLVEntity> {
            var list = mutableListOf<TLVEntity>()
            var currentIndex = 0
            while (currentIndex < buffer.length) {
                var entity = TLVEntity()
                entity.Tag = buffer[currentIndex++].toString()

                entity.Length = buffer.substring(currentIndex, currentIndex + 3).toInt()
                currentIndex += 3
                entity.Value = buffer.substring(currentIndex, currentIndex + entity.Length)
                currentIndex += entity.Length
                list.add(entity)
            }
            return list
        }
    }
}