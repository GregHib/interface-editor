package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.type.WidgetSprite
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.*
import tornadofx.ChangeListener
import tornadofx.add

class SpriteShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    private var defaultSprite: SimpleIntegerProperty? = null
    private var defaultArchive: SimpleIntegerProperty? = null
    private var repeat: BoolProperty? = null
    private val pane = Pane()

    init {
        add(pane)

        reloadSprite()
        val listener = ChangeListener<Any> { _, _, _ -> reloadSprite() }
        getSpriteIndexProperty().addListener(listener)
        getSpriteProperty().addListener(listener)
        repeatProperty().addListener(listener)
        outline.widthProperty().addListener(listener)
        outline.heightProperty().addListener(listener)
    }

    private fun reloadSprite() {
        val id = getSprite()
        val index = getSpriteIndex()
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

    fun getSprite(): Int {
        return getSpriteProperty().get()
    }

    fun getSpriteProperty(): IntegerProperty {
        if (defaultArchive == null)
            defaultArchive = SimpleIntegerProperty(this, "sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ARCHIVE))

        return defaultArchive!!
    }

    fun getSpriteIndex(): Int {
        return getSpriteIndexProperty().get()
    }

    fun getSpriteIndexProperty(): IntegerProperty {
        if (defaultSprite == null)
            defaultSprite = SimpleIntegerProperty(this, "spriteIndex", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return defaultSprite!!
    }

    fun updateArchive(widget: WidgetSprite, id: Int) {
        //Get the number of sprites in archive
        val archive = ArchiveMedia.getDef(id)
        val size = (archive?.sprites?.size ?: 1) - 1

        //Limit the sprite index to archive size
        widget.setDefaultCap(IntValues(0, size))

        //If already on an index which is greater than archive index; reduce, otherwise set the same (refresh)
        widget.setSpriteIndex(if (widget.getSpriteIndex() >= size) size else widget.getSpriteIndex())
    }
}