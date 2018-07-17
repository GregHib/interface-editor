package tests

import com.greg.model.cache.CachePath
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test
import java.io.File

class CacheLoadingTest {

    private val path = CachePath(File("./cache/"))

    @Test
    fun initCache() {
        IndexedFileSystem(path).use { fs ->
            Assert.assertEquals("Error caused while initialising cache", fs.path, path)
        }
    }

    @Test
    fun loadCache() {
        IndexedFileSystem(path).use { fs ->
            Assert.assertTrue("File system failed to load FileStores", fs.load())
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun loadStore() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun mediaArchive() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE)!!)
        }
    }

    @Test
    fun interfaceArchive() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)!!)
        }
    }

    @Test
    fun titleArchive() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE)!!)
        }
    }

}