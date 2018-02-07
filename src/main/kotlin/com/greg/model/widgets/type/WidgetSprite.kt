package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var sprite: SimpleIntegerProperty? = null

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

    init {
        properties.add(spriteProperty())
    }
}