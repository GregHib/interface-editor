package com.greg.model.cache

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.util.*

class CachePathTest {

    val path = CachePath(File("./cache/"))
    val file = CachePath(File("./cache/interface.jag"))
    val invalid = CachePath(File("./cache/invalid.jag"))

    @Test
    fun isValid() {

        IndexedFileSystem(path).use { fs ->
            fs.load()
        }
    }

    @Test
    fun cacheDirectory() {
        Assert.assertTrue(path.isValid())
        Assert.assertFalse(path.isInterfaceFile())
    }

    @Test
    fun interfaceFile() {
        Assert.assertTrue(file.isValid())
        Assert.assertTrue(file.isInterfaceFile())
        Assert.assertFalse(invalid.isValid())

        IndexedFileSystem(path).use { fs ->
            fs.load()
            val buff = fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)
            Assert.assertTrue(Arrays.equals(buff!!.array(),
                    ByteBuffer.wrap(File("./cache/interface.jag").readBytes()).array()))
        }



    }

    @Test
    fun validCache() {
    }
}