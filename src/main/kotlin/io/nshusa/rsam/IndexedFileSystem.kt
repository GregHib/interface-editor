package io.nshusa.rsam

import com.greg.controller.task.tasks.Defragment
import com.greg.model.cache.CachePath
import com.greg.model.cache.formats.CacheFormats
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*

open class IndexedFileSystem : Closeable {

    internal val path: CachePath

    constructor(path: CachePath) {
        this.path = path
        storeCount = if(path.isValid()) path.getIndices(path.getFiles()).size else 0
    }

    constructor(path: String) : this(CachePath(path))

    private val fileStores = arrayOfNulls<FileStore>(255)

    var isLoaded: Boolean = false
        private set

    val storeCount: Int

    fun load(): Boolean {
        if(!path.isValid())
            return false

        if(path.getCacheType() == CacheFormats.FULL_CACHE) {
            val files = path.getFiles()

            val data = path.getDataFile(files) ?: return false

            val indices = path.getIndices(files)

            indices.forEachIndexed { index, file ->
                fileStores[index] = FileStore(index, RandomAccessFile(data, "rw").channel, RandomAccessFile(file, "rw").channel)//TODO does it need a new RAF instance every time?
            }
        }

        isLoaded = true
        return true
    }

    @Throws(IOException::class)
    fun createStore(storeId: Int): Boolean {
        if (storeId < 0 || storeId >= fileStores.size) {
            return false
        }

        if (fileStores[storeId] != null) {
            return false
        }

        val files = path.getFiles()

        val data = path.getDataFile(files) ?: return false

        val indices = path.getIndices(files)

        fileStores[storeId] = FileStore(storeId + 1, RandomAccessFile(data, "rw").channel, RandomAccessFile(indices[storeId], "rw").channel)
        return true
    }

    fun removeStore(storeId: Int): Boolean {
        if (storeId < 0 || storeId >= fileStores.size) {
            return false
        }

        reset()

        try {
            val files = path.getFiles()

            val indices = path.getIndices(files)

            Files.deleteIfExists(indices[storeId].toPath())
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }

    fun defragment() {
        Thread(Defragment(this)).start()
    }

    fun getStore(storeId: Int): FileStore {
        if (storeId < 0 || storeId >= fileStores.size) {
            throw IllegalArgumentException("storeId=$storeId out of range=[0, 254]")
        }

        return fileStores[storeId]!!
    }

    open fun readFile(storeId: Int, fileId: Int): ByteBuffer {
        return getStore(storeId).readFile(fileId)?: ByteBuffer.allocate(0)
    }

    fun create(identifier: String) {
        val dataFile = File(path.path, "$identifier.dat")
        if (!dataFile.exists())
            dataFile.createNewFile()

        for (i in 0 until storeCount) {
            val idxFile = File(path.path, "$identifier.idx$i")
            if (!idxFile.exists())
                idxFile.createNewFile()
        }
    }

    fun reset() {
        try {
            close()
            isLoaded = false
            Arrays.fill(fileStores, null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    override fun close() {
        for (fileStore in fileStores) {
            if (fileStore == null) {
                continue
            }

            fileStore.close()
        }
    }
}