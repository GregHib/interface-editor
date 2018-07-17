package tests

import IntegrationTest
import com.greg.model.cache.CachePath
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import java.io.File

@Category(IntegrationTest::class)
class CacheSpriteLoadingTest {

    private val path = CachePath(File("./cache/"))

    @Test
    fun loadIndex() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE)!!)
            archive.readFile("index.dat")
        }
    }

    @Test
    fun loadSprites(){
        IndexedFileSystem(path).use { fs ->
            fs.load()
            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE)!!)
            val index = archive.readFile("index.dat")


            val sprites = mutableListOf<Sprite>()
            var total = 0
            for (entry in archive.getEntries()) {


                var count = 0
                spriteLoop@ while (true) {
                    try {
                        sprites.add(Sprite.decode(archive, index, entry.hash, count))
                        count++
                    } catch (ex: Exception) {
                        break@spriteLoop
                    }
                }

                total += count
            }

            println("Loaded $total sprites")
            Assert.assertTrue(total > 0)
        }
    }
}