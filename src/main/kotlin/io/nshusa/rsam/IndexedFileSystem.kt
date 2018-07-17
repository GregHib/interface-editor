package io.nshusa.rsam

import com.greg.model.cache.CachePath
import java.io.Closeable
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*

open class IndexedFileSystem(val path: CachePath) : Closeable {

    private val fileStores = arrayOfNulls<FileStore>(255)

    var isLoaded: Boolean = false
        private set

    val storeCount = path.indices?.size ?: 0

    fun load(): Boolean {
        if(!path.isValid())
            return false

        if(!path.isInterfaceFile()) {
            val data = path.data ?: return false

            val indices = path.indices ?: return false

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

        val data = path.data ?: return false

        val indices = path.indices ?: return false

        fileStores[storeId] = FileStore(storeId + 1, RandomAccessFile(data, "rw").channel, RandomAccessFile(indices[storeId], "rw").channel)
        return true
    }

    fun removeStore(storeId: Int): Boolean {
        if (storeId < 0 || storeId >= fileStores.size) {
            return false
        }

        reset()

        try {
            val indices = path.indices ?: return false

            Files.deleteIfExists(indices[storeId].toPath())
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return false
    }

    fun defragment(): Boolean {
        try {
            if (!isLoaded) {
                return false
            }

            val map = LinkedHashMap<Int, MutableList<ByteBuffer>>()

            val files = path.getFiles()

            files.forEachIndexed { store, _ ->
                val fileStore = getStore(store)

                map[fileStore.storeId] = ArrayList()

                for (file in 0 until fileStore.fileCount) {
                    val buffer = fileStore.readFile(file) ?: continue

                    val data = map[store]
                    data!!.add(buffer)
                }
            }

            reset()

            val data = path.data ?: return false

            val indices = path.indices ?: return false

            Files.deleteIfExists(data.toPath())

            indices.forEach { Files.deleteIfExists(it.toPath()) }

            load()

            for ((fileStoreId, value) in map) {

                val fileStore = getStore(fileStoreId)

                for (file in 0 until value.size)
                    fileStore.writeFile(file, value[file].array())

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }

        return true
    }

    fun getStore(storeId: Int): FileStore {
        if (storeId < 0 || storeId >= fileStores.size) {
            throw IllegalArgumentException("storeId=$storeId out of range=[0, 254]")
        }

        return fileStores[storeId]!!
    }

    open fun readFile(storeId: Int, fileId: Int): ByteBuffer? {
        return getStore(storeId).readFile(fileId)
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