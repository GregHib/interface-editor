package tests

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import org.junit.Assert
import org.junit.Test
import java.nio.file.Paths

class CacheLoading {

    @Test
    fun initCache() {
        val path = Paths.get("./cache/")
        val fs = IndexedFileSystem.init(path)
        Assert.assertEquals("Error caused while initialising cache at $path", fs.getRoot(), path)
    }

    @Test
    fun loadCache() {
        val fs = IndexedFileSystem.init(Paths.get("./cache/"))
        Assert.assertTrue("File system failed to load FileStores", fs.load())
    }

    @Test
    fun loadStore() {
        IndexedFileSystem.init(Paths.get("./cache/")).use { fs ->
            fs.load()
            fs.getStore(FileStore.ARCHIVE_FILE_STORE)
        }
    }

    @Test
    fun loadArchive() {
        IndexedFileSystem.init(Paths.get("./cache/")).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
        }
    }

    @Test
    fun loadIndex() {
        IndexedFileSystem.init(Paths.get("./cache/")).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            val archive = Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
            archive.readFile("index.dat")
        }
    }

    @Test
    fun loadSprites(){
        IndexedFileSystem.init(Paths.get("./cache/")).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            val archive = Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
            val index = archive.readFile("index.dat")


            var total = 0
            for (entry in archive.getEntries()) {


                var sprites = 0
                spriteLoop@ while (true) {
                    try {
                        val sprite = Sprite.decode(archive, index, entry.hash, sprites)
                        sprites++
                    } catch (ex: Exception) {
                        break@spriteLoop
                    }

                }

                total += sprites
            }

            Assert.assertTrue(total > 0)
        }
    }
}