package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.view.sprites.SpriteController
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty

open class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var cap: ObjectProperty<IntRange>? = null
    private var sprite: IntegerProperty? = null

    fun getSprite(): Int {
        return spriteProperty().get()
    }

    fun setSprite(value: Int) {
        spriteProperty().set(value)
    }

    fun spriteProperty(): IntegerProperty {
        if (sprite == null)
            sprite = SimpleIntegerProperty(this, "sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return sprite!!
    }


    fun setCap(range: IntRange) {
        capProperty().set(range)
    }

    fun capProperty(): ObjectProperty<IntRange> {
        if (cap == null)
            cap = SimpleObjectProperty(this, "cap", IntRange(0, SpriteController.filteredExternal.size))

        return cap!!
    }

    init {
        properties.addCapped(spriteProperty(), capProperty())
    }
}