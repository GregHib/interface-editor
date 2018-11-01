package com.greg.view

import com.greg.model.cache.Cache
import com.greg.model.cache.CachePath
import com.greg.model.cache.archives.font.Font
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import tornadofx.App
import tornadofx.View
import tornadofx.imageview
import tornadofx.vbox
import java.awt.image.BufferedImage


class FontTest : View() {

    private val path = CachePath("./cache/")
    private var cache = Cache(path)
    lateinit var view: ImageView

    override val root = vbox {

        view = imageview {
            translateY = 10.0
            prefWidth = 100.0
            prefHeight = 100.0
        }
    }

    init {
        cache.load()
        cache.use { fs ->

            val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))

            val font = Font.decode(archive, "p11_full", false)

            val text = "A longer line\nOr two"
            val colour = 0xb31a1a

            val rgba = toRgba(colour)

            val textImage = getImage(font, text, true, rgba)

            view.image = SwingFXUtils.toFXImage(textImage, null)
        }
    }

    private fun getImage(font: Font, text: String, shadow: Boolean, colour: Int, alpha: Int = 255): BufferedImage? {
        return font.getAsImage(text, shadow, false, colour, alpha)
    }

    private fun toRgba(colour: Int, alpha: Int = 255): Int {
        val r = (colour shr 16 and 0xff)
        val g = (colour shr 8 and 0xff)
        val b = (colour and 0xff)
        return alpha shl 24 or (r shl 16) or (g shl 8) or b
    }
}

class FontTestApp: App(FontTest::class)

fun main(args: Array<String>) {
    Application.launch(FontTestApp::class.java, *args)
}