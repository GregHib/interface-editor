package com.greg.controller.model

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.widget.type.WidgetType
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var text: StringProperty? = null
    private var colour: ObjectProperty<Color>? = null

    init {
        properties.add(textProperty(), WidgetType.TEXT)
        properties.add(colourProperty(), WidgetType.TEXT)
    }

    fun setText(value: String) { textProperty().set(value) }
    fun getText(): String { return textProperty().get() }
    fun textProperty(): StringProperty {
        if (text == null)
            text = SimpleStringProperty(this, "text", "Text")

        return text!!
    }

    fun setColour(value: Color) { colourProperty().set(value) }
    fun getColour(): Color { return colourProperty().get() }
    fun colourProperty(): ObjectProperty<Color> {
        if (colour == null)
            colour = SimpleObjectProperty<Color>(this, "colour", Settings.getColour(SettingsKey.DEFAULT_TEXT_COLOUR))

        return colour!!
    }
}