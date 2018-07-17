package io.nshusa.rsam.util

import com.greg.model.cache.CachePath
import io.nshusa.rsam.IndexedFileSystem
import java.io.File
import java.io.IOException

object RsamUtils {

    fun defragment(fs: IndexedFileSystem) {


    }


    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        IndexedFileSystem(CachePath(File("cache"))).use { fs ->

            val dir = File("./cache/defragmented/")

            if (dir.exists()) {
                dir.mkdirs()
            }

            val dataFile = File(dir, "main_file_cache.dat")

            if (!dataFile.exists()) {
                dataFile.createNewFile()
            }

            for (i in 0 until fs.storeCount) {
                val idxFile = File(dir, "main_file_cache.idx$i")

                if (!idxFile.exists()) {
                    idxFile.createNewFile()
                }
            }

            val nFs = IndexedFileSystem(CachePath(dir))

            for (storeCount in 0 until fs.storeCount) {

                val fileStore = fs.getStore(storeCount) ?: continue
                val fileStoreCopy = nFs.getStore(storeCount) ?: continue

                println("defragmenting index: $storeCount")

                for (file in 0 until fileStore.fileCount) {
                    val buffer = fileStore.readFile(file)
                    fileStoreCopy.writeFile(file, if (buffer == null) ByteArray(0) else buffer.array())
                    println("copying file: $file")
                }

            }

            nFs.close()

            println("finished")

        }
    }

}