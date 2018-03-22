package com.greg.view.canvas.widgets

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.view.sprites.SpriteController
import javafx.scene.image.ImageView
import tornadofx.add

class CacheSpriteShape(id: Int, width: Int, height: Int) : SpriteShape(id, width, height) {

    var archive: StringProperty? = null
    private val image = ImageView()

    init {
        add(image)
        loadSprite()
        archiveProperty().addListener { _, _, _ ->
            loadSprite()
        }
    }

    override fun loadSprite() {
        val archive = SpriteController.getArchive("${getArchive()}.dat")
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
}