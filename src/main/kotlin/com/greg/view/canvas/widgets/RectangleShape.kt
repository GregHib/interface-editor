package com.greg.view.canvas.widgets

import com.greg.model.widgets.type.WidgetRectangle
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import tornadofx.add

class RectangleShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {

    val rectangle = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())

    init {
        add(rectangle)
        rectangle.strokeType = StrokeType.INSIDE
        rectangle.widthProperty().bind(outline.widthProperty())
        rectangle.heightProperty().bind(outline.heightProperty())
    }

    fun updateColour(widget: WidgetRectangle, hover: Boolean = false, forceSecondary: Boolean = false) {
        val colour = if(forceSecondary)
            if(hover) widget.getSecondaryHoverColour() else widget.getSecondaryColour()
        else
            if(hover) widget.getDefaultHoverColour() else widget.getDefaultColour()

        rectangle.fill = if(widget.isFilled()) colour else Color.TRANSPARENT
        rectangle.stroke = colour
    }
}