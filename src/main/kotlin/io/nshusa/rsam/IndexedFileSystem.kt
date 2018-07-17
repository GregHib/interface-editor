package io.nshusa.rsam

import java.io.Closeable
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class IndexedFileSystem private constructor(private var root: Path?) : Closeable {

    private val fileStores = arrayOfNulls<FileStore>(255)

    var isLoaded: Boolean = false
        private set

    val storeCount: Int
        get() {
            var count = 0
            for (i in 0..254) {
                val indexPath = root!!.resolve("main_file_cache.idx$i")
                if (Files.exists(indexPath)) {
                    count++
                }
            }

            return count
        }

    fun load(): Boolean {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root)
            }

            val dataPath = root!!.resolve("main_file_cache.dat")

            if (!Files.exists(dataPath)) {
                return false
            }

            for (i in 0..254) {
                val indexPath = root!!.resolve("main_file_cache.idx$i")
                if (Files.exists(indexPath)) {
                    fileStores[i] = FileStore(i, RandomAccessFile(dataPath.toFile(), "rw").channel, RandomAccessFile(indexPath.toFile(), "rw").channel)
                }
            }
            isLoaded = true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }

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

        val dataPath = root!!.resolve("main_file_cache.dat")

        if (!Files.exists(dataPath)) {
            Files.createFile(dataPath)
        }

        val path = root!!.resolve("main_file_cache.idx$storeId")

        if (!Files.exists(path)) {
            Files.createFile(path)
        }
        fileStores[storeId] = FileStore(storeId + 1, RandomAccessFile(dataPath.toFile(), "rw").channel, RandomAccessFile(path.toFile(), "rw").channel)
        return true
    }

    fun removeStore(storeId: Int): Boolean {
        if (storeId < 0 || storeId >= fileStores.size) {
            return false
        }

        reset()

        try {
            Files.deleteIfExists(root!!.resolve("main_file_cache.idx$storeId"))
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

            val files = root!!.toFile().listFiles() ?: return false

            val map = LinkedHashMap<Int, MutableList<ByteBuffer>>()

            for (store in 0..254) {

                val fileStore = getStore(store) ?: continue

                map[fileStore.storeId] = ArrayList()

                for (file in 0 until fileStore.fileCount) {
                    val buffer = fileStore.readFile(file) ?: continue

                    val data = map[store]
                    data!!.add(buffer)
                }

            }

            reset()

            Files.deleteIfExists(root!!.resolve("main_file_cache.dat"))

            for (i in fileStores.indices) {
                Files.deleteIfExists(root!!.resolve("main_file.cache.idx$i"))
            }

            load()

            for ((fileStoreId, value) in map) {

                val fileStore = getStore(fileStoreId)

                for (file in 0 until value.size) {
                    val data = value[file]
                    fileStore!!.writeFile(file, if (data == null) ByteArray(0) else data.array())
                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }

        return true
    }

    fun getStore(storeId: Int): FileStore? {
        if (storeId < 0 || storeId >= fileStores.size) {
            throw IllegalArgumentException(String.format("storeId=%d out of range=[0, 254]", storeId))
        }

        return fileStores[storeId]
    }

    fun readFile(storeId: Int, fileId: Int): ByteBuffer? {
        val store = getStore(storeId)
        return store!!.readFile(fileId)
    }

    fun getRoot(): Path? {
        return root
    }

    internal fun setRoot(root: Path) {
        reset()
        this.root = root
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

    companion object {

        fun init(root: Path): IndexedFileSystem {
            return IndexedFileSystem(root)
        }
    }

}