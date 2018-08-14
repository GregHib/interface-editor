package com.greg.model.cache

import com.greg.model.cache.formats.CacheFormats
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import java.nio.ByteBuffer

class Cache(path: CachePath) : IndexedFileSystem(path) {

    init {
        if(path.isValid())
            load()
    }

    override fun readFile(storeId: Int, fileId: Int): ByteBuffer {
        if(storeId == FileStore.ARCHIVE_FILE_STORE && path.getCacheType() == CacheFormats.UNPACKED_CACHE)
            return ByteBuffer.wrap(path.getArchiveFile(path.getFiles(), fileId - 1).readBytes())
        return super.readFile(storeId, fileId)
    }

    fun save(): Boolean {
        if(!path.isValid())
            return false


        when(path.getCacheType()) {
            CacheFormats.FULL_CACHE -> {
                //Replace
                //Defrag
            }
            CacheFormats.UNPACKED_CACHE -> {
                //Overwrite
            }
        }


        return true
    }
}