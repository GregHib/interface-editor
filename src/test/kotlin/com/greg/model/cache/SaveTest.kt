package com.greg.model.cache

import com.greg.model.cache.archives.widget.WidgetDataIO
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.ByteBuffer

class SaveTest {
    val cache = OldCache(CachePath("./interface.jag"))
    val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

    fun load(buffer: ByteBuffer = archive.readFile("data")): Boolean {
        return WidgetDataIO.read(buffer) != null
    }
}

fun main(args: Array<String>) {
    val test = SaveTest()


    val archiveFile = test.cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)
    val archive = Archive.decode(archiveFile)

    println("File ${archiveFile.array().size}")

    val dataFile = archive.readFile("data")

    test.load(dataFile)
    val buffer = WidgetDataIO.write()

    println("Archive file ${archiveFile.array().size}")
    println("Data file ${dataFile.array().size}")
    println("Buffer file ${buffer.array().size}")

    println("Replace ${archive.writeFile("data", buffer.array())}")

    val encoded = archive.encode()


    FileUtils.writeByteArrayToFile(File("save.jag"), encoded)
//    println("Write ${test.cache.writeFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE, encoded)}")
}