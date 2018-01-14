package com.greg.controller.view

import com.greg.controller.model.Widget
import com.greg.ui.canvas.widget.type.WidgetType

class WidgetShapeBuilder(val widget: Widget) {
    fun build(): WidgetShape {
        return when(widget.type) {
            WidgetType.TEXT -> TextShape(widget.identifier, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight())
            else -> {
                WidgetShape(widget.identifier, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight())
            }
        }
    }
}