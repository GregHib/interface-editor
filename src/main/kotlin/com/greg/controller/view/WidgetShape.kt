package com.greg.controller.view

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.Group
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import tornadofx.add

open class WidgetShape(val identifier: Int, x: Int, y: Int, width: Int, height: Int) : Group() {
    val rectangle = Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    init {
        add(rectangle)

        rectangle.stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        rectangle.strokeType = StrokeType.INSIDE

        layoutXProperty().bind(rectangle.xProperty())
        layoutYProperty().bind(rectangle.yProperty())
    }

}