package com.greg.view.sprites

import com.greg.view.sprites.tree.ImageArchive
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.util.HashUtils
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import tornadofx.Controller
import java.io.InputStream

class SpriteController : Controller() {
    companion object {

        val placeholderIcon = Image(SpriteController::class.java.getResourceAsStream("placeholder.png"))

        var imageArchiveList: ObservableList<ImageArchive> = FXCollections.observableArrayList()

        fun getArchive(archive: String): ImageArchive? {
            return imageArchiveList.firstOrNull { it.hash == HashUtils.nameToHash(archive) }
        }
    }

    fun getInternalArchiveNames(): List<String> {
        return imageArchiveList.map { it -> getName(it.hash) }
    }

    private fun importInternal() {
        IndexedFileSystem("./cache/").use { fs ->
            fs.load()
            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
            val index = archive.readFile("index.dat")


            var total = 0
            for (entry in archive.getEntries()) {

                val sprites = mutableListOf<Sprite>()

                spriteLoop@ while (true) {
                    try {
                        sprites.add(Sprite.decode(archive, index, entry.hash, sprites.size))
                    } catch (ex: Exception) {
                        break@spriteLoop
                    }

                }

                imageArchiveList.add(ImageArchive(entry.hash, sprites))

                total += sprites.size
            }


            println("Loaded $total sprites")
        }

    }

    fun start() {
        //Quick start method for developing
        importInternal()
    }

    fun getName(hash: Int): String {
        //Get known hashes
        val inputStream: InputStream = javaClass.getResourceAsStream("4.txt")
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        //If match return (without the .dat)
        lineList.forEach {
            if (HashUtils.nameToHash(it) == hash)
                return it.substring(0, it.length - 4)
        }
        //Otherwise just return the hash id
        return hash.toString()
    }
}
