package com.greg.model.cache.archives

class Buffer {
    private var payload = arrayListOf<Byte>()

    fun writeByte(i: Int) {
        payload.add(i.toByte())
    }

    fun writeByte(i: Byte) {
        payload.add(i)
    }

    fun writeInt(i: Int) {
        payload.add((i shr 24).toByte())
        payload.add((i shr 16).toByte())
        payload.add((i shr 8).toByte())
        payload.add(i.toByte())
    }

    fun writeShort(i: Int) {
        payload.add((i shr 8).toByte())
        payload.add(i.toByte())
    }

    fun writeString(string: String) {
        string.forEach { writeByte(it.toByte()) }
        writeByte(10)
    }

    fun array(): ByteArray {
        return payload.toByteArray()
    }
}