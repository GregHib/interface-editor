package com.greg.model.cache.archives

import com.greg.model.cache.Cache
import com.greg.model.cache.archives.font.Font
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive

class ArchiveFont : CacheArchive() {

    val fonts = arrayListOf<Font>()

    override fun reset(): Boolean {
        fonts.clear()
        return true
    }

    override fun load(cache: Cache): Boolean {
        return try {
            val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))

            fonts.add(Font.decode(archive, "p11_full", false))
            fonts.add(Font.decode(archive, "p12_full", false))
            fonts.add(Font.decode(archive, "b12_full", false))
            fonts.add(Font.decode(archive, "q8_full", true))
            true
        } catch (e: NullPointerException) {
            e.printStackTrace()
            cache.reset()
            false
        }
    }

}