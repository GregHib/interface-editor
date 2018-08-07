package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var text: StringProperty? = null
    private var colour: ObjProperty<Color>? = null

    init {
        properties.add(textProperty())
        properties.add(colourProperty())
    }

    fun setText(value: String) {
        textProperty().set(value)
    }

    fun getText(): String {
        return textProperty().get()
    }

    fun textProperty(): StringProperty {
        if (text == null)
            text = StringProperty(this, "text", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))

        return text!!
    }

    fun setColour(value: Color) {
        colourProperty().set(value)
    }

    fun getColour(): Color {
        return colourProperty().get()
    }

    fun colourProperty(): ObjProperty<Color> {
        if (colour == null)
            colour = ObjProperty(this, "colour", Settings.getColour(Settings.DEFAULT_TEXT_COLOUR))

        return colour!!
    }
}