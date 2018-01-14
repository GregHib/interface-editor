package com.greg.controller.model

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var text: StringProperty? = null

    fun setText(value: String) { textProperty().set(value) }
    fun getText(): String { return textProperty().get() }
    fun textProperty(): StringProperty {
        if (text == null)
            text = SimpleStringProperty(this, "text", "Text")

        return text!!
    }

    private var colour: ObjectProperty<Color>? = null

    fun setColour(value: Color) { colourProperty().set(value) }
    fun getColour(): Color { return colourProperty().get() }
    fun colourProperty(): ObjectProperty<Color> {
        if (colour == null)
            colour = SimpleObjectProperty<Color>(this, "colour", Color.BLACK)

        return colour!!
    }
}