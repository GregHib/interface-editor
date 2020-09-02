package com.greg.model.cache

import com.greg.model.cache.formats.CacheFormats
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.util.*

class CachePathTest {

    private val path = CachePath("./cache/")
    private val file = CachePath("./cache/interface.jag")
    private val invalidPath = CachePath("./cache/invalid/")
    private val invalidFile = CachePath("./cache/invalid.jag")

    @Test
    fun cacheDirectory() {
        Assert.assertTrue(path.isValid())
        Assert.assertTrue(path.getCacheType() == CacheFormats.FULL_CACHE)
    }

    @Test
    fun interfaceFile() {
        Assert.assertTrue(file.isValid())
        Assert.assertTrue(file.getCacheType() == CacheFormats.UNPACKED_CACHE)

        OldCache(file).use { fs ->
            val buff = fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)
            Assert.assertTrue(Arrays.equals(buff.array(),
                    ByteBuffer.wrap(File("./cache/interface.jag").readBytes()).array()))
        }
    }

    @Test
    fun invalidCache() {
        Assert.assertFalse(invalidPath.isValid())
        Assert.assertFalse(invalidFile.isValid())
    }
}