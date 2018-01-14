package com.greg.controller.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var colour: ObjectProperty<Color>? = null

    fun setColour(value: Color) { colourProperty().set(value) }
    fun getColour(): Color { return colourProperty().get() }
    fun colourProperty(): ObjectProperty<Color> {
        if (colour == null)
            colour = SimpleObjectProperty<Color>(this, "colour", Color.BLACK)

        return colour!!
    }
}