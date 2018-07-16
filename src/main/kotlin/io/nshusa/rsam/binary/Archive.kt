package io.nshusa.rsam.binary

import io.nshusa.rsam.util.ByteBufferUtils
import io.nshusa.rsam.util.CompressionUtils
import io.nshusa.rsam.util.HashUtils

import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.zip.CRC32
import java.util.zip.Checksum

class Archive(entries: Array<ArchiveEntry?>) {

    var isExtracted: Boolean = false
        private set

    private val entries = LinkedHashMap<Int, ArchiveEntry>()

    val entryCount: Int
        get() = entries.size

    class ArchiveEntry(val hash: Int, val uncompressedSize: Int, val compresseedSize: Int, val data: ByteArray)

    init {
        Arrays.asList(*entries).forEach { it -> this.entries[it!!.hash] = it }
    }

    @Synchronized
    @Throws(IOException::class)
    fun encode(): ByteArray {
        var size = 2 + entries.size * 10

        for (file in entries.values) {
            size += file.compresseedSize
        }

        var buffer: ByteBuffer
        if (!isExtracted) {
            buffer = ByteBuffer.allocate(size + 6)
            ByteBufferUtils.write24Int(buffer, size)
            ByteBufferUtils.write24Int(buffer, size)
        } else {
            buffer = ByteBuffer.allocate(size)
        }

        buffer.putShort(entries.size.toShort())

        for (entry in entries.values) {
            buffer.putInt(entry.hash)
            ByteBufferUtils.write24Int(buffer, entry.uncompressedSize)
            ByteBufferUtils.write24Int(buffer, entry.compresseedSize)
        }

        for (file in entries.values) {
            buffer.put(file.data)
        }

        val data: ByteArray
        if (!isExtracted) {
            data = buffer.array()
        } else {
            val unzipped = buffer.array()
            val zipped = CompressionUtils.bzip2(unzipped)
            if (unzipped.size == zipped.size) {
                throw RuntimeException("error zipped size matches original")
            }
            buffer = ByteBuffer.allocate(zipped.size + 6)
            ByteBufferUtils.write24Int(buffer, unzipped.size)
            ByteBufferUtils.write24Int(buffer, zipped.size)
            buffer.put(zipped, 0, zipped.size)
            data = buffer.array()
        }

        return data

    }

    @Throws(IOException::class)
    fun readFile(name: String): ByteBuffer {
        return readFile(HashUtils.nameToHash(name))
    }

    @Throws(IOException::class)
    fun readFile(hash: Int): ByteBuffer {
        for (entry in entries.values) {

            if (entry.hash != hash) {
                continue
            }

            if (!isExtracted) {
                val decompressed = ByteArray(entry.uncompressedSize)
                CompressionUtils.debzip2(entry.data, decompressed)
                return ByteBuffer.wrap(decompressed)
            } else {
                return ByteBuffer.wrap(entry.data)
            }

        }
        throw FileNotFoundException(String.format("file=%d could not be found.", hash))
    }

    @Throws(IOException::class)
    fun replaceFile(oldHash: Int, newName: String, data: ByteArray): Boolean {
        return replaceFile(oldHash, HashUtils.nameToHash(newName), data)
    }

    @Throws(IOException::class)
    fun replaceFile(oldHash: Int, newHash: Int, data: ByteArray): Boolean {
        if (entries.containsKey(oldHash)) {
            return false
        }

        val entry: ArchiveEntry
        if (!isExtracted) {
            val compressed = CompressionUtils.bzip2(data)
            entry = Archive.ArchiveEntry(newHash, data.size, compressed.size, compressed)
        } else {
            entry = Archive.ArchiveEntry(newHash, data.size, data.size, data)
        }

        entries.replace(oldHash, entry)
        return true
    }

    @Throws(IOException::class)
    fun writeFile(name: String, data: ByteArray): Boolean {
        return writeFile(HashUtils.nameToHash(name), data)
    }

    @Throws(IOException::class)
    fun writeFile(hash: Int, data: ByteArray): Boolean {
        if (entries.containsKey(hash)) {
            replaceFile(hash, hash, data)
        }

        val entry: ArchiveEntry
        if (!isExtracted) {
            val compressed = CompressionUtils.bzip2(data)
            entry = Archive.ArchiveEntry(hash, data.size, compressed.size, compressed)
        } else {
            entry = Archive.ArchiveEntry(hash, data.size, data.size, data)
        }

        entries[hash] = entry
        return true
    }

    fun rename(oldHash: Int, newName: String): Boolean {
        return rename(oldHash, HashUtils.nameToHash(newName))
    }

    fun rename(oldHash: Int, newHash: Int): Boolean {
        if (!entries.containsKey(oldHash)) {
            return false
        }

        val old = entries[oldHash] ?: return false

        entries.replace(oldHash, ArchiveEntry(newHash, old.uncompressedSize, old.compresseedSize, old.data))
        return true
    }

    @Throws(FileNotFoundException::class)
    fun getEntry(name: String): ArchiveEntry? {
        return getEntry(HashUtils.nameToHash(name))
    }

    @Throws(FileNotFoundException::class)
    fun getEntry(hash: Int): ArchiveEntry? {
        if (entries.containsKey(hash)) {
            return entries[hash]
        }

        throw FileNotFoundException(String.format("Could not find entry: %d.", hash))
    }

    @Throws(IOException::class)
    fun getEntryAt(index: Int): ArchiveEntry {
        if (index >= entries.size) {
            throw FileNotFoundException(String.format("File at index=%d could not be found.", index))
        }

        var pos = 0
        for (entry in entries.values) {
            if (pos == index) {
                return entry
            }
            pos++
        }

        throw FileNotFoundException(String.format("File at index=%d could not be found.", index))
    }

    fun indexOf(name: String): Int {
        return indexOf(HashUtils.nameToHash(name))
    }

    fun indexOf(hash: Int): Int {
        var index = 0
        for (entry in entries.values) {
            if (entry.hash == hash) {
                return index
            }
            index++
        }

        return -1
    }

    operator fun contains(name: String): Boolean {
        return contains(HashUtils.nameToHash(name))
    }

    operator fun contains(hash: Int): Boolean {
        return entries.containsKey(hash)
    }

    fun remove(name: String): Boolean {
        return remove(HashUtils.nameToHash(name))
    }

    fun remove(hash: Int): Boolean {
        if (entries.containsKey(hash)) {
            entries.remove(hash)
            return true
        }
        return false
    }

    fun getEntries(): Array<ArchiveEntry> {
        return entries.values.toTypedArray()
    }

    companion object {

        val TITLE_ARCHIVE = 1
        val CONFIG_ARCHIVE = 2
        val INTERFACE_ARCHIVE = 3
        val MEDIA_ARCHIVE = 4
        val VERSION_LIST_ARCHIVE = 5
        val TEXTURE_ARCHIVE = 6
        val WORDENC_ARCHIVE = 7
        val SOUND_ARCHIVE = 8

        @Throws(IOException::class)
        fun decode(buffer: ByteBuffer): Archive {
            var buffer = buffer
            val uncompressedLength = ByteBufferUtils.readU24Int(buffer)
            val compressedLength = ByteBufferUtils.readU24Int(buffer)

            var extracted = false

            if (uncompressedLength != compressedLength) {
                val compressed = ByteArray(compressedLength)
                val decompressed = ByteArray(uncompressedLength)
                buffer.get(compressed)
                CompressionUtils.debzip2(compressed, decompressed)
                buffer = ByteBuffer.wrap(decompressed)
                extracted = true
            }

            val entries = buffer.short.toInt() and 0xFFFF

            val hashes = IntArray(entries)
            val uncompressedSizes = IntArray(entries)
            val compressedSizes = IntArray(entries)

            val archiveEntries = arrayOfNulls<ArchiveEntry>(entries)

            val entryBuf = ByteBuffer.wrap(buffer.array())
            entryBuf.position(buffer.position() + entries * 10)

            for (i in 0 until entries) {

                hashes[i] = buffer.int
                uncompressedSizes[i] = ByteBufferUtils.readU24Int(buffer)
                compressedSizes[i] = ByteBufferUtils.readU24Int(buffer)

                val entryData = ByteArray(compressedSizes[i])
                entryBuf.get(entryData)

                archiveEntries[i] = ArchiveEntry(hashes[i], uncompressedSizes[i], compressedSizes[i], entryData)
            }

            val archive = Archive(archiveEntries)
            archive.isExtracted = extracted

            return archive
        }

        @Throws(IOException::class)
        fun encodeChecksum(archives: Array<Archive>): ByteBuffer {
            // Integer.BYTES represents the crc stores as an integer which has 4 bytes, + Intger.BYTES because a pre calculated value is after the crcs which is in the form of a int as well
            val buffer = ByteBuffer.allocate(archives.size * Integer.BYTES + Integer.BYTES)

            val checksum = CRC32()

            val crcs = IntArray(archives.size)

            for (i in 1 until crcs.size) {
                val archive = archives[i]

                checksum.reset()

                val encoded = archive.encode()

                checksum.update(encoded, 0, encoded.size)

                val crc = checksum.value.toInt()

                crcs[i] = crc

                buffer.putInt(crc)
            }

            // predefined value
            var calculated = 1234

            for (index in archives.indices) {
                calculated = (calculated shl 1) + crcs[index]
            }

            buffer.putShort(calculated.toShort())

            return buffer
        }
    }

}