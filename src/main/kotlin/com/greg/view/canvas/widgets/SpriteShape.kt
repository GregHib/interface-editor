package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.StringProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.ImageView
import tornadofx.add
import java.awt.image.BufferedImage

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

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

                    layoutX = sprite.offsetX.toDouble()
                    layoutY = sprite.offsetY.toDouble()
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

        image.image = resample(bufferedImage)

        outline.width = bufferedImage.width.toDouble()
        outline.height = bufferedImage.height.toDouble()
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