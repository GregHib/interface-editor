package tests

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths

class CacheLoadingTest {

    private val path = Paths.get("./cache/")

    @Test
    fun initCache() {
        val fs = IndexedFileSystem.init(path)
        Assert.assertEquals("Error caused while initialising cache at $path", fs.getRoot(), path)
    }

    @Test
    fun loadCache() {
        val fs = IndexedFileSystem.init(path)
        Assert.assertTrue("File system failed to load FileStores", fs.load())
    }

    @Test
    fun loadStore() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun mediaArchive() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
        }
    }

    @Test
    fun interfaceArchive() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            Archive.decode(store!!.readFile(Archive.INTERFACE_ARCHIVE)!!)
        }
    }

    @Test
    fun titleArchive() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            Archive.decode(store!!.readFile(Archive.TITLE_ARCHIVE)!!)
        }
    }

}