package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var colour: ObjProperty<Color>? = null

    init {
        properties.add(colourProperty())
    }

    fun setColour(value: Color) { colourProperty().set(value) }

    fun getColour(): Color { return colourProperty().get() }

    fun colourProperty(): ObjProperty<Color> {
        if (colour == null)
            colour = ObjProperty(this, "colour", Settings.getColour(Settings.DEFAULT_RECTANGLE_COLOUR))

        return colour!!
    }
}