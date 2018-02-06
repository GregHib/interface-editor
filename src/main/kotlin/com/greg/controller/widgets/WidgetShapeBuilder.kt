package com.greg.controller.widgets

import com.greg.model.widgets.Widget
import com.greg.model.widgets.WidgetType
import com.greg.view.canvas.widgets.RectangleShape
import com.greg.view.canvas.widgets.TextShape
import com.greg.view.canvas.widgets.WidgetShape

class WidgetShapeBuilder(val widget: Widget) {
    fun build(): WidgetShape {
        return when(widget.type) {
            WidgetType.TEXT -> TextShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.RECTANGLE -> RectangleShape(widget.identifier, widget.getWidth(), widget.getHeight())
            else -> {
                WidgetShape(widget.identifier, widget.getWidth(), widget.getHeight())
            }
        }
    }
}