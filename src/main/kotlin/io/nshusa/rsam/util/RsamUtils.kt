package io.nshusa.rsam.util

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFileAttributes

object RsamUtils {

    fun defragment(fs: IndexedFileSystem) {


    }


    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        IndexedFileSystem.init(Paths.get("cache")).use { fs ->

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

            val nFs = IndexedFileSystem.init(dir.toPath())

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