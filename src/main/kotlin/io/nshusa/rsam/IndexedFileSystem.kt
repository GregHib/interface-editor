package io.nshusa.rsam

import com.greg.model.cache.Cache
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
    private val fileStores = arrayOfNulls<FileStore>(255)

    var isLoaded: Boolean = false
        private set
    var storeCount: Int = 0
    internal lateinit var path: CachePath

    constructor(path: CachePath) {
        setPath(path)
    }

    constructor(path: String) : this(CachePath(path))

    fun setPath(path: CachePath) {
        if(isLoaded)
            reset()
        this.path = path
        storeCount = if (path.isValid()) path.getIndices(path.getFiles()).size else 0
    }

    fun load(): Boolean {
        if (!path.isValid())
            return false

        if (path.getCacheType() == CacheFormats.FULL_CACHE) {
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

    fun defrag2(): Boolean {
        val dir = File("./defragmented_cache/")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val dataFile = File(dir, "main_file_cache.dat")
        if (!dataFile.exists()) {
            dataFile.createNewFile()
        }

        for (i in 0 until storeCount) {
            val idxFile = File(dir, "main_file_cache.idx$i")
            if (!idxFile.exists())
                idxFile.createNewFile()
        }


        val nFs = Cache(CachePath(dir.toPath()))

        var var26: Throwable? = null
        try {
            val stores = storeCount

            for (i in 0 until storeCount) {
                val store = getStore(i)
                val copy = nFs.getStore(i)

                val files = store.fileCount

                for (file in 0..files) {
                    val buffer = store.readFile(file)
                    copy.writeFile(file, buffer?.array() ?: ByteArray(0))
                    val progress = i + 1 / stores * 100.0
                }
            }
        } catch (t: Throwable) {
            var26 = t
            throw t
        } finally {
            if (var26 != null) {
                try {
                    nFs.close()
                } catch (var21: Throwable) {
                    var26.addSuppressed(var21)
                }
            } else {
                nFs.close()
            }

        }
        return true
    }

    fun defragment(): Boolean {
        try {
            if (!isLoaded || path.getCacheType() != CacheFormats.FULL_CACHE)
                return false

            val map = LinkedHashMap<Int, MutableList<ByteBuffer>>()

            val files = path.getFiles()

            for (store in 0 until storeCount) {
                val fileStore = getStore(store)

                val list = ArrayList<ByteBuffer>()

                for (file in 0 until fileStore.fileCount) {
                    val buffer = fileStore.readFile(file) ?: continue

                    list.add(buffer)
                }
                map[fileStore.storeId] = list
            }

            val identifier = path.getIdentifier(path.getFiles())

            reset()

            val data = path.getDataFile(files) ?: return false

            val indices = path.getIndices(files)

            Files.deleteIfExists(data.toPath())

            indices.forEach { Files.deleteIfExists(it.toPath()) }

            create(identifier ?: "main_file_cache")

            load()

            for ((fileStoreId, value) in map) {
                val fileStore = getStore(fileStoreId)

                for (file in 0 until value.size)
                    fileStore.writeFile(file, value[file].array())
            }

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }/* finally {
            close()
        }*/
    }

    fun getStore(storeId: Int): FileStore {
        if (storeId < 0 || storeId >= fileStores.size) {
            throw IllegalArgumentException("storeId=$storeId out of range=[0, 254]")
        }

        return fileStores[storeId]!!
    }

    open fun readFile(storeId: Int, fileId: Int): ByteBuffer {
        return getStore(storeId).readFile(fileId) ?: ByteBuffer.allocate(0)
    }

    fun writeFile(storeId: Int, fileId: Int, data: ByteArray): Boolean {
        return getStore(storeId).writeFile(fileId, data)
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