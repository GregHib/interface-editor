package com.greg.model.cache

import com.greg.model.cache.archives.font.Font
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class CacheTest {

    private val path = CachePath("./cache/")
    private var cache = OldCache(path)

    private val filePath = CachePath("./cache/interface.jag")
    private var fileCache = OldCache(filePath)

    @Test
    fun readFile() {
        fileCache.use { fs ->
            try {
                Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
                Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))
                Assert.fail()
            } catch (e: NullPointerException) {
            }
        }
    }

    @Test
    fun loadSprites() {
//        Assert.assertTrue(cache.loadSprites() > 0)
//        Assert.assertTrue(fileCache.loadSprites() == 0)
    }

    @Test
    fun loadFonts() {
        cache.use { fs ->

            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))

            loadFont(archive, "p11_full", false)
            loadFont(archive, "p12_full", false)
            loadFont(archive, "b12_full", false)
            loadFont(archive, "q8_full", true)
        }
    }

    private fun loadFont(archive: Archive, name: String, wideSpace: Boolean) {
        try {
            Font.decode(archive, name, wideSpace)
        } catch (e: IOException) {
            e.printStackTrace()
            Assert.fail()
        }
    }
}