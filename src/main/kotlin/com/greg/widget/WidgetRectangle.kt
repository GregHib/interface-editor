package com.greg.widget

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

open class WidgetRectangle(x: Double, y: Double, width: Double, height: Double): Widget() {

    override fun setStroke(colour: Color?) {
        rectangle.stroke = colour
    }

    var rectangle = Rectangle(x, y, width, height)

    init {
        this.children.add(rectangle)
    }
}