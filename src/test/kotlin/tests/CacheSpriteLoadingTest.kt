package tests

import IntegrationTest
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import java.nio.file.Paths

@Category(IntegrationTest::class)
class CacheSpriteLoadingTest {

    private val path = Paths.get("./cache/")

    @Test
    fun loadIndex() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            val archive = Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
            archive.readFile("index.dat")
        }
    }

    @Test
    fun loadSprites(){
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            val archive = Archive.decode(store!!.readFile(Archive.MEDIA_ARCHIVE)!!)
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