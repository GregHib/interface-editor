package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.WidgetSprite
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.ImageView
import tornadofx.ChangeListener
import tornadofx.add

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    private var defaultSprite: SimpleIntegerProperty? = null
    private var secondarySprite: SimpleIntegerProperty? = null
    private var defaultArchive: StringProperty? = null
    private var secondaryArchive: StringProperty? = null
    private val image = ImageView()
    var flip = false

    init {
        add(image)
        loadSprite()
        val listener = ChangeListener<Any> { _, _, _ -> loadSprite() }
        defaultSpriteProperty().addListener(listener)
        secondarySpriteProperty().addListener(listener)
        defaultArchiveProperty().addListener(listener)
        secondaryArchiveProperty().addListener(listener)
    }

    private fun loadSprite() {
        val archive = ArchiveMedia.getImage("${if(flip) getSecondaryArchive() else getDefaultArchive()}.dat")
        if (archive != null) {
            val spriteIndex = if(flip) getSecondarySprite() else getDefaultSprite()
            if (spriteIndex >= 0 && spriteIndex < archive.sprites.size) {
                val sprite = archive.sprites[spriteIndex]
                if(sprite != null) {
                    val bufferedImage = sprite.toBufferedImage()
                    displayImage(image, bufferedImage, outline)

                    layoutX = sprite.offsetX.toDouble()
                    layoutY = sprite.offsetY.toDouble()
                }
            }
        } else {
            displayImage(image, null, outline)
        }
    }

    fun getDefaultArchive(): String {
        return defaultArchiveProperty().get()
    }

    fun defaultArchiveProperty(): StringProperty {
        if (defaultArchive == null)
            defaultArchive = StringProperty("defaultArchive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))

        return defaultArchive!!
    }

    fun getSecondaryArchive(): String {
        return secondaryArchiveProperty().get()
    }

    fun secondaryArchiveProperty(): StringProperty {
        if (secondaryArchive == null)
            secondaryArchive = StringProperty("secondaryArchive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))

        return secondaryArchive!!
    }

    fun getDefaultSprite(): Int {
        return defaultSpriteProperty().get()
    }

    fun defaultSpriteProperty(): IntegerProperty {
        if (defaultSprite == null)
            defaultSprite = SimpleIntegerProperty(this, "defaultSprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return defaultSprite!!
    }

    fun getSecondarySprite(): Int {
        return secondarySpriteProperty().get()
    }

    fun secondarySpriteProperty(): IntegerProperty {
        if (secondarySprite == null)
            secondarySprite = SimpleIntegerProperty(this, "secondarySprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return secondarySprite!!
    }

    fun updateArchive(widget: WidgetSprite, archiveName: String, default: Boolean) {
        //Get the number of sprites in archive
        val archive = ArchiveMedia.getImage("$archiveName.dat")//TODO the gnome hash isn't .dat? are all .dat?
        val size = (archive?.sprites?.size ?: 1) - 1

        //Limit the sprite index to archive size
        if(default)
            widget.setDefaultCap(IntValues(0, size))
        else
            widget.setSecondaryCap(IntValues(0, size))

        //If already on an index which is greater than archive index; reduce, otherwise set the same (refresh)
        if(default)
            widget.setDefaultSprite(if (widget.getDefaultSprite() >= size) size else widget.getDefaultSprite())
        else
            widget.setSecondarySprite(if (widget.getSecondarySprite() >= size) size else widget.getSecondarySprite())
    }
}