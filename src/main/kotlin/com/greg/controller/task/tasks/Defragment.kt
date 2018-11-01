package com.greg.controller.task.tasks

import com.greg.model.cache.formats.CacheFormats
import io.nshusa.rsam.IndexedFileSystem
import javafx.concurrent.Task
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.*

class Defragment(private val cache: IndexedFileSystem) : Task<Boolean>() {

    override fun call(): Boolean {
        try {
            if (!cache.isLoaded || cache.path.getCacheType() != CacheFormats.FULL_CACHE)
                return false

            val map = LinkedHashMap<Int, MutableList<ByteBuffer>>()

            val files = cache.path.getFiles()

            for(store in 0 until cache.storeCount) {
                val fileStore = cache.getStore(store)

                val list = ArrayList<ByteBuffer>()

                for (file in 0 until fileStore.fileCount) {
                    val buffer = fileStore.readFile(file) ?: continue

                    list.add(buffer)
                }
                map[fileStore.storeId] = list
            }

            val identifier = cache.path.getIdentifier(cache.path.getFiles())

            cache.reset()

            val data = cache.path.getDataFile(files) ?: return false

            val indices = cache.path.getIndices(files)

            Files.deleteIfExists(data.toPath())

            indices.forEach { Files.deleteIfExists(it.toPath()) }

            cache.create(identifier ?: "main_file_cache")

            cache.load()

            for ((fileStoreId, value) in map) {
                val fileStore = cache.getStore(fileStoreId)

                for (file in 0 until value.size)
                    fileStore.writeFile(file, value[file].array())
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }/* finally {
            cache.close()
        }*/

        return true
    }

}