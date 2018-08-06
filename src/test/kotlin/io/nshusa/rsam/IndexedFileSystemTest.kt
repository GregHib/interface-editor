package io.nshusa.rsam

import com.greg.model.cache.CachePath
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test

class IndexedFileSystemTest {

    private val path = CachePath("./cache/")

    @Test
    fun initCache() {
        IndexedFileSystem(path).use { fs ->
            Assert.assertEquals("Error caused while initialising cache", fs.path, path)
        }
    }

    @Test
    fun load() {
        IndexedFileSystem(path).use { fs ->
            Assert.assertTrue("File system failed to load FileStores", fs.load())
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun getStore() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun mediaArchive() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
        }
    }

    @Test
    fun readFile() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))
        }
    }

    @Test
    fun reset() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            fs.reset()
            Assert.assertFalse(fs.isLoaded)
        }
    }

}