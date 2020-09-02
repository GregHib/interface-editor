package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.WidgetSprite
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import tornadofx.ChangeListener
import tornadofx.add

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    private var defaultSprite: SimpleIntegerProperty? = null
    private var secondarySprite: SimpleIntegerProperty? = null
    private var defaultArchive: StringProperty? = null
    private var secondaryArchive: StringProperty? = null
    private var repeat: BoolProperty? = null
    private val pane = Pane()
    private val image = ImageView()
    var flip = false

    init {
        println("$width $height")
        add(pane)
//        add(image)
        loadSprite(width, height)
        val listener = ChangeListener<Any> { _, _, _ -> loadSprite(width, height) }
        defaultSpriteProperty().addListener(listener)
        secondarySpriteProperty().addListener(listener)
        defaultArchiveProperty().addListener(listener)
        secondaryArchiveProperty().addListener(listener)
        repeatProperty().addListener(listener)
        outline.widthProperty().addListener(listener)
        outline.heightProperty().addListener(listener)
    }

    private fun loadSprite(width: Int, height: Int) {
        val archive = ArchiveMedia.getImage(if(flip) getSecondaryArchive() else getDefaultArchive())
        if (archive != null) {
            val spriteIndex = if(flip) getSecondarySprite() else getDefaultSprite()
            if (spriteIndex >= 0 && spriteIndex < archive.sprites.size) {
                val sprite = archive.sprites[spriteIndex]
                if(sprite != null) {
                    // TODO
                    val bufferedImage = sprite//.toBufferedImage()
//                    displayImage(image, bufferedImage, outline)
                    pane.prefWidth = width.toDouble()
                    pane.prefHeight = height.toDouble()
                    pane.background = Background(BackgroundImage(resample(bufferedImage), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT))
                    layoutX = 0.0//sprite.offsetX.toDouble()
                    layoutY = 0.0//sprite.offsetY.toDouble()
                }
            }
        } else {
            displayImage(image, null, outline)
        }
    }

    fun repeatProperty(): BoolProperty {
        if (repeat == null)
            repeat = BoolProperty("repeat", false)

        return repeat!!
    }

    fun isRepeat(): Boolean {
        return repeat?.get() ?: false
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
        val archive = ArchiveMedia.getImage(archiveName)
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