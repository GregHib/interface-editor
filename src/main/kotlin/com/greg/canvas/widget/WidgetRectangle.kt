package com.greg.canvas.widget

import com.greg.properties.Property
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.scene.shape.Rectangle

class WidgetRectangle(x: Double, y: Double, width: Double, height: Double) : Rectangle(x, y, width, height), WidgetInterface {

    var properties = mutableListOf<Property>()

    init {
        stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        properties.add(Property("Background fill", "fillProperty", ObjectProperty::class, PropertyType.COLOUR_PICKER, this::class))
        properties.add(Property("Width", "widthProperty", DoubleProperty::class, PropertyType.NUMBER_FIELD, this::class))
        properties.add(Property("Width", "heightProperty", DoubleProperty::class, PropertyType.NUMBER_FIELD, this::class))
//        properties.add(Property("Location X", "layoutXProperty", DoubleProperty::class, PropertyType.NUMBER_FIELD, this::class))
//        properties.add(Property("Location Y", "layoutYProperty", DoubleProperty::class, PropertyType.NUMBER_FIELD, this::class))
    }

    override fun getWidgetProperties(): List<Property>? {
        return properties
    }
}