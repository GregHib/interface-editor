package com.greg.canvas.widget

import com.greg.panels.attributes.AttributeType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.Node
import javafx.scene.shape.Rectangle

class WidgetRectangle : AttributeWidget {

    private val rectangle: Rectangle

    constructor(x: Double = Settings.getDouble(SettingsKey.DEFAULT_POSITION_X) + 0.5, y: Double = Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y) + 0.5, width: Double = Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH) - 1, height: Double = Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT) - 1) {
        rectangle = Rectangle(x, y, width, height)
        rectangle.stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)

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