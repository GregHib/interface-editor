package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.WidgetSprite
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.*
import tornadofx.ChangeListener
import tornadofx.add

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    private var defaultSprite: SimpleIntegerProperty? = null
    private var secondarySprite: SimpleIntegerProperty? = null
    private var defaultArchive: SimpleIntegerProperty? = null
    private var secondaryArchive: SimpleIntegerProperty? = null
    private var repeat: BoolProperty? = null
    private val pane = Pane()
    var secondary = false

    init {
        add(pane)

        reloadSprite()
        val listener = ChangeListener<Any> { _, _, _ -> reloadSprite() }
        defaultSpriteProperty().addListener(listener)
        secondarySpriteProperty().addListener(listener)
        defaultArchiveProperty().addListener(listener)
        secondaryArchiveProperty().addListener(listener)
        repeatProperty().addListener(listener)
        outline.widthProperty().addListener(listener)
        outline.heightProperty().addListener(listener)
    }

    private fun reloadSprite() {
        val id = if(secondary) getSecondaryArchive() else getDefaultArchive()
        val index = if(secondary) getSecondarySprite() else getDefaultSprite()
        val image = ArchiveMedia.getImage(id, index)
        if(image != null) {
            pane.prefWidth = outline.width
            pane.prefHeight = outline.height
            pane.background = Background(BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT))
        } else {
            pane.background = null
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

    fun getDefaultArchive(): Int {
        return defaultArchiveProperty().get()
    }

    fun defaultArchiveProperty(): IntegerProperty {
        if (defaultArchive == null)
            defaultArchive = SimpleIntegerProperty(this, "defaultArchive", Settings.getInt(Settings.DEFAULT_SPRITE_ARCHIVE))

        return defaultArchive!!
    }

    fun getSecondaryArchive(): Int {
        return secondaryArchiveProperty().get()
    }

    fun secondaryArchiveProperty(): IntegerProperty {
        if (secondaryArchive == null)
            secondaryArchive = SimpleIntegerProperty(this, "secondaryArchive", Settings.getInt(Settings.DEFAULT_SPRITE_ARCHIVE))

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

    fun updateArchive(widget: WidgetSprite, archiveName: Int, default: Boolean) {
        //Get the number of sprites in archive
        val archive = ArchiveMedia.getDef(archiveName)
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