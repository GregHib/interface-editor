package com.greg.canvas.widget.types.impl

import com.greg.canvas.widget.AttributeWidget
import com.greg.panels.attributes.AttributeType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType

class WidgetRectangle : AttributeWidget {

    private val rectangle: Rectangle

    constructor(x: Double = Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), y: Double = Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), width: Double = Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH), height: Double = Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT)) {
        rectangle = Rectangle(x, y, width, height)
        rectangle.stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        rectangle.strokeType = StrokeType.INSIDE

        attributes.addProperty("Background fill", "fillProperty", AttributeType.COLOUR_PICKER)
        attributes.addProperty("Width", "widthProperty", AttributeType.NUMBER_FIELD)
        attributes.addProperty("Height", "heightProperty", AttributeType.NUMBER_FIELD)
    }

    override fun getNode(): Node {
        return getRectangle()
    }

    fun getRectangle(): Rectangle {
        return rectangle
    }
}