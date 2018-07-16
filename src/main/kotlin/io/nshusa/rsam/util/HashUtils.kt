package io.nshusa.rsam.util

object HashUtils {

    fun nameToHash(name: String): Int {
        var name = name
        var hash = 0
        name = name.toUpperCase()
        for (i in 0 until name.length) {
            hash = hash * 61 + name[i].toInt() - 32
        }
        return hash
    }

    fun hashSpriteName(name: String): Long {
        var name = name
        name = name.toUpperCase()
        var hash: Long = 0
        for (index in 0 until name.length) {
            hash = hash * 61 + name[index].toLong() - 32
            hash = hash + (hash shr 56) and 0xFFFFFFFFFFFFFFL
        }

        return hash
    }

}