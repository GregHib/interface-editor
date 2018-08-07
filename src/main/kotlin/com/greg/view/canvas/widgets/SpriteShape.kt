package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.StringProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import tornadofx.add
import java.awt.image.BufferedImage

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {

    var sprite: SimpleIntegerProperty? = null
    var archive: StringProperty? = null
    private val image = ImageView()

    init {
        add(image)
        loadSprite()
        spriteProperty().addListener { _, _, _ ->
            loadSprite()
        }
        archiveProperty().addListener { _, _, _ ->
            loadSprite()
        }
    }

    private fun loadSprite() {
        val archive = ArchiveMedia.getImage("${getArchive()}.dat")
        if (archive != null) {
            if (getSprite() >= 0 && getSprite() < archive.sprites.size) {
                val sprite = archive.sprites[getSprite()]
                if(sprite != null) {
                    val bufferedImage = sprite.toBufferedImage()
                    displayImage(bufferedImage)
                }
            }
        }
    }

    fun getArchive(): String {
        return archiveProperty().get()
    }

    fun archiveProperty(): StringProperty {
        if (archive == null)
            archive = StringProperty(this, "archive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))

        return archive!!
    }

    private fun displayImage(bufferedImage: BufferedImage) {
        image.fitWidth = bufferedImage.width.toDouble()
        image.fitHeight = bufferedImage.height.toDouble()
        image.isPreserveRatio = true
        image.image = resample(SwingFXUtils.toFXImage(bufferedImage, null), 10)

        //TODO fix width/height on adding sprite 0
        outline.width = bufferedImage.width.toDouble()
        outline.height = bufferedImage.height.toDouble()
    }

    private fun resample(input: Image, scaleFactor: Int): Image {
        val w = input.width.toInt()
        val h = input.height.toInt()

        val output = WritableImage(w * scaleFactor, h * scaleFactor)

        val reader = input.pixelReader
        val writer = output.pixelWriter

        val colour = Settings.getInt(Settings.SPRITE_BACKGROUND_COLOUR)
        val red = (colour shr 16) and 0xFF
        val green = (colour shr 8) and 0xFF
        val blue = colour and 0xFF

        for (y in 0 until h) {
            for (x in 0 until w) {
                var argb = reader.getArgb(x, y)

                //Replace colour with transparent
                val r = argb shr 16 and 0xFF
                val g = argb shr 8 and 0xFF
                val b = argb and 0xFF

                if (r == red && g == green && b == blue)
                    argb = argb and 0x00FFFFFF

                //Scale
                for (dy in 0 until scaleFactor) {
                    for (dx in 0 until scaleFactor) {
                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb)
                    }
                }
            }
        }

        return output
    }

    fun getSprite(): Int {
        return spriteProperty().get()
    }

    fun spriteProperty(): IntegerProperty {
        if (sprite == null)
            sprite = SimpleIntegerProperty(this, "sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return sprite!!
    }
}