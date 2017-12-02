package com.greg.canvas.widget

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

open class WidgetRectangle(x: Double, y: Double, width: Double, height: Double): Widget() {

    override fun setStroke(colour: Color?) {
        rectangle.stroke = colour
    }

    var rectangle = Rectangle(x, y, width, height)

    init {
        setStroke(Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
        this.children.add(rectangle)
    }
}