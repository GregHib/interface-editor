package com.greg.ui.canvas.widget.type.types

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.panel.panels.attribute.AttributeType
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType

class WidgetRectangle(x: Int = Settings.getInt(SettingsKey.DEFAULT_POSITION_X), y: Int = Settings.getInt(SettingsKey.DEFAULT_POSITION_Y), width: Int = Settings.getInt(SettingsKey.DEFAULT_RECTANGLE_WIDTH), height: Int = Settings.getInt(SettingsKey.DEFAULT_RECTANGLE_HEIGHT)) : WidgetFacade() {

    private val rectangle: Rectangle = Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    init {
        rectangle.stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        rectangle.strokeType = StrokeType.INSIDE
        attributes.addProperty("Background fill", "fillProperty", AttributeType.COLOUR_PICKER)
        attributes.addProperty("Width", "widthProperty", AttributeType.NUMBER_FIELD)
        attributes.addProperty("Height", "heightProperty", AttributeType.NUMBER_FIELD)
    }

    fun getRectangle(): Rectangle {
        return rectangle
    }

    override fun getNode(): Node {
        return getRectangle()
    }
}