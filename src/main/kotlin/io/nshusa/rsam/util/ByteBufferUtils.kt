package io.nshusa.rsam.util

import java.nio.ByteBuffer

object ByteBufferUtils {

    fun write24Int(buffer: ByteBuffer, value: Int) {
        buffer.put((value shr 16).toByte()).put((value shr 8).toByte()).put(value.toByte())
    }

    fun getUMedium(buffer: ByteBuffer): Int {
        return buffer.short.toInt() and 0xFFFF shl 8 or (buffer.get().toInt() and 0xFF)
    }

    fun getUShort(buffer: ByteBuffer): Int {
        return buffer.short.toInt() and 0xffff
    }

    fun readU24Int(buffer: ByteBuffer): Int {
        return buffer.get().toInt() and 0x0ff shl 16 or (buffer.get().toInt() and 0x0ff shl 8) or (buffer.get().toInt() and 0x0ff)
    }

    fun getSmart(buffer: ByteBuffer): Int {
        val peek = buffer.get(buffer.position()).toInt() and 0xFF
        return if (peek < 128) {
            buffer.get().toInt() and 0xFF
        } else (buffer.short.toInt() and 0xFFFF) - 32768
    }

    fun getString(buffer: ByteBuffer): String {
        val bldr = StringBuilder()
        var b: Byte
        while (buffer.hasRemaining()) {
            b = buffer.get()
            if(b.toInt() == 10)
                break
            bldr.append(b.toChar())
        }
        return bldr.toString()
    }

}