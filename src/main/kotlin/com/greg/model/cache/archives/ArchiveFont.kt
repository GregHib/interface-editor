package com.greg.model.cache.archives

import com.greg.model.cache.OldCache
import com.greg.model.cache.archives.font.Font
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import rs.dusk.cache.Cache

class ArchiveFont : CacheArchive() {

    val fonts = arrayListOf<Font>()
    companion object {

        val shadow = DropShadow()
        val small: javafx.scene.text.Font = javafx.scene.text.Font.font(11.0)
        val medium: javafx.scene.text.Font = javafx.scene.text.Font.font(12.0)
        val bold: javafx.scene.text.Font = javafx.scene.text.Font.font(null, FontWeight.BLACK, 12.0)
        val thin: javafx.scene.text.Font = javafx.scene.text.Font.font(null, FontWeight.THIN, 14.0)

        init {
            shadow.blurType = BlurType.GAUSSIAN
            shadow.radius = 1.0
            shadow.offsetX = 1.0
            shadow.offsetY = 1.0
            shadow.color = Color.color(0.0, 0.0, 0.0)
        }
    }

    override fun reset(): Boolean {
        fonts.clear()
        return true
    }

    override fun load(cache: Cache): Boolean {
        return false/*try {
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
        }*/
    }

}