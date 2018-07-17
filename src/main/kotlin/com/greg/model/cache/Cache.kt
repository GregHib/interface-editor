package com.greg.model.cache

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import tornadofx.runAsync
import java.nio.ByteBuffer

class Cache(path: CachePath) : IndexedFileSystem(path) {

    init {
        load()

        runAsync {
            loadSprites()
        }
    }

    override fun readFile(storeId: Int, fileId: Int): ByteBuffer {
        if(path.getCacheType() == CacheTypes.UNPACKED_CACHE && storeId == FileStore.ARCHIVE_FILE_STORE)
            return ByteBuffer.wrap(path.getArchiveFile(path.getFiles(), fileId)?.readBytes())
        return super.readFile(storeId, fileId)
    }

    fun loadSprites(): Int {
        val archive = Archive.decode(readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
        val index = archive.readFile("index.dat")

        val sprites = mutableListOf<Sprite>()
        for (entry in archive.getEntries()) {

            spriteLoop@ while (true) {
                try {
                    sprites.add(Sprite.decode(archive, index, entry.hash, sprites.size))
                } catch (ex: Exception) {
                    break@spriteLoop
                }
            }
        }

        return sprites.size
    }


    fun loadFonts() {

    }

    fun loadInterface() {

    }
}