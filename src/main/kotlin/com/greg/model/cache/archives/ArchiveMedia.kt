package com.greg.model.cache.archives

import com.greg.view.sprites.tree.ImageArchive
import io.nshusa.rsam.util.HashUtils
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import rs.dusk.cache.Cache
import rs.dusk.cache.definition.data.SpriteGroup
import rs.dusk.cache.definition.decoder.SpriteDecoder
import rs.dusk.core.io.read.BufferReader
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.util.logging.Level
import javax.imageio.ImageIO

class ArchiveMedia : CacheArchive() {

    companion object {
        lateinit var cache: Cache
        val decoder = SpriteDecoder()
        var imageArchive: ObservableList<SpriteGroup> = FXCollections.observableArrayList()

        fun getImage(name: String): SpriteGroup? {
            val id = name.toIntOrNull() ?: return null
            var group = imageArchive.firstOrNull { it.hash == id }
            if(group != null) {
                return group
            }
            val data = cache.getFile(8, id) ?: return null
            group = decoder.decode(BufferReader(data), id)
            imageArchive.add(group)
            return group
        }
    }

    fun getArchive(archive: String): SpriteGroup? {
        return imageArchive.firstOrNull { it.hash == HashUtils.nameToHash(archive) }
    }

    fun getInternalArchiveNames(): List<String> {
        return imageArchive.map { getName(it.hash) }
    }

    override fun load(cache: Cache): Boolean {
        ArchiveMedia.cache = cache
//        val decoder = SpriteDecoder()
//        repeat(cache.lastArchiveId(8)) { id ->
//
//            val data = cache.getFile(8, id) ?: return false
//            val group = decoder.decode(BufferReader(data), id)
//            imageArchive.add(group)
//        }
        println("Loaded ${cache.lastArchiveId(8)} sprite groups.")

        return true/*try {
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
        }*/
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