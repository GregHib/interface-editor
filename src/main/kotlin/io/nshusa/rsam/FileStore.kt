package io.nshusa.rsam

import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.util.ByteBufferUtils

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.zip.CRC32
import java.util.zip.Checksum
import kotlin.experimental.and

class FileStore(val storeId: Int, private val dataChannel: FileChannel, private val metaChannel: FileChannel) {

    private val checksum = CRC32()

    val fileCount: Int
        get() {
            if (!metaChannel.isOpen) {
                return 0
            }

            try {
                return Math.toIntExact(metaChannel.size() / META_BLOCK_LENGTH)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return 0
        }

    @Throws(IOException::class)
    fun calculateChecksum(updateArchive: Archive, fileId: Int): Int {
        // you can't calculate the checksum for archives like this they don't have an associated version and crc file in the version list archive
        if (storeId == 0) {
            return 0
        }

        val versionBuf = updateArchive.readFile(versionFileNames[storeId - 1])

        val versionCount = versionBuf.capacity() / java.lang.Short.BYTES

        var version = 0

        if (fileId < versionCount) {
            versionBuf.position(fileId * java.lang.Short.BYTES)

            version = versionBuf.short.toInt() and 0xFFFF
        }

        // read the file
        val fileBuf = readFile(fileId) ?: return 0

// file data first then version after
        val buf = ByteBuffer.allocate(fileBuf.capacity() + java.lang.Short.BYTES)
        buf.put(fileBuf)
        buf.putShort(version.toShort())

        checksum.reset()
        checksum.update(buf.array(), 0, buf.capacity())

        return checksum.value.toInt()
    }

    @Synchronized
    fun readFile(fileId: Int): ByteBuffer? {
        try {

            if (fileId * META_BLOCK_LENGTH + META_BLOCK_LENGTH > metaChannel.size()) {
                return null
            }

            buffer.position(0).limit(META_BLOCK_LENGTH)
            metaChannel.read(buffer, (fileId * META_BLOCK_LENGTH).toLong())
            buffer.flip()

            val size = ByteBufferUtils.readU24Int(buffer)
            var block = ByteBufferUtils.readU24Int(buffer)

            if (block <= 0 || block.toLong() > dataChannel.size() / 520L) {
                return null
            }

            val fileBuffer = ByteBuffer.allocate(size)

            var remaining = size
            var chunk = 0
            val blockLength = if (fileId <= 0xFFFF) BLOCK_LENGTH else EXPANDED_BLOCK_LENGTH
            val headerLength = if (fileId <= 0xFFFF) HEADER_LENGTH else EXPANDED_HEADER_LENGTH

            while (remaining > 0) {
                if (block == 0) {
                    return null
                }

                val blockSize = if (remaining > blockLength) blockLength else remaining
                buffer.position(0).limit(blockSize + headerLength)
                dataChannel.read(buffer, (block * TOTAL_BLOCK_LENGTH).toLong())
                buffer.flip()

                val currentFile: Int
                val currentChunk: Int
                val nextBlock: Int
                val currentIndex: Int

                if (fileId <= 65535) {
                    currentFile = buffer.short.toInt() and 0xFFFF
                    currentChunk = buffer.short.toInt() and 0xFFFF
                    nextBlock = ByteBufferUtils.readU24Int(buffer)
                    currentIndex = buffer.get().toInt() and 0xFF
                } else {
                    currentFile = buffer.int
                    currentChunk = buffer.short.toInt() and 0xFFFF
                    nextBlock = ByteBufferUtils.readU24Int(buffer)
                    currentIndex = buffer.get().toInt() and 0xFF
                }

                if (fileId != currentFile || chunk != currentChunk || storeId + 1 != currentIndex) {
                    return null
                }
                if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                    return null
                }

                val rem = buffer.remaining()

                for (i in 0 until rem) {
                    fileBuffer.put(buffer.get())
                }

                remaining -= blockSize
                block = nextBlock
                chunk++
            }
            fileBuffer.position(0)
            return fileBuffer
        } catch (_ex: IOException) {
            _ex.printStackTrace()
            return null
        }

    }

    @Synchronized
    fun writeFile(id: Int, data: ByteArray): Boolean {
        return writeFile(id, data, true) || writeFile(id, data, false)
    }

    @Synchronized
    private fun writeFile(fileId: Int, data: ByteArray, exists: Boolean): Boolean {
        var exists = exists
        try {

            val dataBuf = ByteBuffer.wrap(data)

            var block: Int

            if (exists) {

                if (fileId * META_BLOCK_LENGTH + META_BLOCK_LENGTH > metaChannel.size()) {
                    return false
                }

                buffer.position(0).limit(META_BLOCK_LENGTH)
                metaChannel.read(buffer, (fileId * META_BLOCK_LENGTH).toLong())
                buffer.flip()

                // skip size
                buffer.position(3)

                block = ByteBufferUtils.readU24Int(buffer)

                if (block <= 0 || block.toLong() > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                    return false
                }

            } else {
                block = ((dataChannel.size() + TOTAL_BLOCK_LENGTH - 1) / TOTAL_BLOCK_LENGTH).toInt()

                if (block == 0) {
                    block = 1
                }

            }

            buffer.position(0)
            ByteBufferUtils.write24Int(buffer, data.size)
            ByteBufferUtils.write24Int(buffer, block)
            buffer.flip()

            metaChannel.write(buffer, (fileId * META_BLOCK_LENGTH).toLong())

            var remaining = data.size
            var chunk = 0
            val blockLength = if (fileId <= 0xFFFF) BLOCK_LENGTH else EXPANDED_BLOCK_LENGTH
            val headerLength = if (fileId <= 0xFFFF) HEADER_LENGTH else EXPANDED_HEADER_LENGTH
            while (remaining > 0) {
                var nextBlock = 0

                if (exists) {
                    buffer.position(0).limit(headerLength)
                    dataChannel.read(buffer, (block * TOTAL_BLOCK_LENGTH).toLong())
                    buffer.flip()

                    val currentFile: Int
                    val currentChunk: Int
                    val currentIndex: Int
                    if (fileId <= 0xFFFF) {
                        currentFile = buffer.short.toInt() and 0xFFFF
                        currentChunk = buffer.short.toInt() and 0xFFFF
                        nextBlock = ByteBufferUtils.readU24Int(buffer)
                        currentIndex = buffer.get().toInt() and 0xFF
                    } else {
                        currentFile = buffer.int
                        currentChunk = buffer.short.toInt() and 0xFFFF
                        nextBlock = ByteBufferUtils.readU24Int(buffer)
                        currentIndex = buffer.get().toInt() and 0xFF
                    }

                    if (fileId != currentFile || chunk != currentChunk || storeId + 1 != currentIndex) {
                        return false
                    }

                    if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LENGTH) {
                        return false
                    }

                }

                if (nextBlock == 0) {
                    exists = false
                    nextBlock = ((dataChannel.size() + TOTAL_BLOCK_LENGTH - 1) / TOTAL_BLOCK_LENGTH).toInt()

                    if (nextBlock == 0) {
                        nextBlock = 1
                    }

                    if (nextBlock == block) {
                        nextBlock++
                    }

                }

                if (remaining <= blockLength) {
                    nextBlock = 0
                }

                buffer.position(0).limit(TOTAL_BLOCK_LENGTH)

                if (fileId <= 0xFFFF) {
                    buffer.putShort(fileId.toShort())
                    buffer.putShort(chunk.toShort())
                    ByteBufferUtils.write24Int(buffer, nextBlock)
                    buffer.put((storeId + 1).toByte())
                } else {
                    buffer.putInt(fileId)
                    buffer.putShort(chunk.toShort())
                    ByteBufferUtils.write24Int(buffer, nextBlock)
                    buffer.put((storeId + 1).toByte())
                }

                val blockSize = if (remaining > blockLength) blockLength else remaining
                dataBuf.limit(dataBuf.position() + blockSize)
                buffer.put(dataBuf)
                buffer.flip()

                dataChannel.write(buffer, (block * TOTAL_BLOCK_LENGTH).toLong())
                remaining -= blockSize
                block = nextBlock
                chunk++
            }

            return true
        } catch (ex: IOException) {
            return false
        }

    }

    fun close() {
        try {
            dataChannel.close()
            metaChannel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val crcFileNames = arrayOf("model_crc", "anim_crc", "midi_crc", "map_crc")
        private val versionFileNames = arrayOf("model_version", "anim_version", "midi_version", "map_version")

        val ARCHIVE_FILE_STORE = 0
        val MODEL_FILE_STORE = 1
        val ANIMATION_FILE_STORE = 2
        val MIDI_FILE_STORE = 3
        val MAP_FILE_STORE = 4

        private val EXPANDED_HEADER_LENGTH = 10
        private val HEADER_LENGTH = 8

        private val EXPANDED_BLOCK_LENGTH = 510
        private val BLOCK_LENGTH = 512

        private val TOTAL_BLOCK_LENGTH = HEADER_LENGTH + BLOCK_LENGTH
        private val META_BLOCK_LENGTH = 6

        private val buffer = ByteBuffer.allocate(BLOCK_LENGTH + HEADER_LENGTH)
    }

}