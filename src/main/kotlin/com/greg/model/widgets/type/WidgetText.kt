package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    var text = ObjProperty(this, "text", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))
    var colour = ObjProperty(this, "colour", Settings.getColour(Settings.DEFAULT_TEXT_COLOUR))

    fun setText(value: String) {
        text.set(value)
    }

    fun setColour(value: Color) {
        colour.set(value)
    }

    init {
        properties.add(text)
        properties.add(colour)
    }
}