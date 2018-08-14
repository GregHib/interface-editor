package com.greg.model.cache.archives

import com.greg.model.cache.Cache
import com.greg.view.sprites.tree.ImageArchive
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.util.HashUtils
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.io.InputStream

class ArchiveMedia : CacheArchive() {

    companion object {
        var imageArchive: ObservableList<ImageArchive> = FXCollections.observableArrayList()

        fun getImage(name: String): ImageArchive? {
            return imageArchive.firstOrNull { it.hash == HashUtils.nameToHash(name) }
        }
    }

    fun getArchive(archive: String): ImageArchive? {
        return imageArchive.firstOrNull { it.hash == HashUtils.nameToHash(archive) }
    }

    fun getInternalArchiveNames(): List<String> {
        return imageArchive.map { it -> getName(it.hash) }
    }

    override fun load(cache: Cache): Boolean {
        return try {
            val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
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

                if(entry.hash == -1929337337)//Index
                    continue

                imageArchive.add(ImageArchive(entry.hash, sprites))

                total += sprites.size
            }

            println("Loaded $total sprites")
            true
        } catch (e : NullPointerException) {
            e.printStackTrace()
            cache.reset()
            false
        }
    }

    override fun reset(): Boolean {
        imageArchive.clear()
        return true
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