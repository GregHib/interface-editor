
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.ImageArchive

object Test {

    fun run() {
        IndexedFileSystem("./cache/").use { fs ->
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)

            val mediaArchive = Archive.decode(store.readFile(Archive.MEDIA_ARCHIVE)!!)

            ImageArchive.decode(mediaArchive, "number_button.dat")

            ImageArchive.decode(mediaArchive, 1165431679)
        }
    }
}

fun main(args: Array<String>) {
    Test.run()
}