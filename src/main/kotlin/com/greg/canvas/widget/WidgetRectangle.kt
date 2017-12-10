package com.greg.canvas.widget

import com.greg.panels.attributes.AttributePaneType
import com.greg.properties.Property
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.shape.Rectangle

class WidgetRectangle(x: Double, y: Double, width: Double, height: Double) : Rectangle(x, y, width, height), WidgetInterface {

    var properties = mutableListOf<Property>()

    init {
        stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        properties.add(Property("Background fill", "fillProperty", PropertyType.COLOUR_PICKER, this::class))
        properties.add(Property("Width", "widthProperty", PropertyType.NUMBER_FIELD, this::class))
        properties.add(Property("Height", "heightProperty", PropertyType.NUMBER_FIELD, this::class))
    }

    override fun getProperties(type: AttributePaneType): List<Property>? {
        if(type == AttributePaneType.PROPERTIES)
            return properties
        return null
    }
}