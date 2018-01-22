package com.greg.model.widgets

import com.greg.settings.Settings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    var text = SimpleStringProperty(this, "text", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))
    var colour = SimpleObjectProperty(this, "colour", Settings.getColour(Settings.DEFAULT_TEXT_COLOUR))

    init {
        properties.add(text)
        properties.add(colour)
    }
}