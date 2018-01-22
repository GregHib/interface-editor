package src.com.greg.controller.widgets

import src.com.greg.model.widgets.Widget
import src.com.greg.model.widgets.WidgetType
import src.com.greg.view.WidgetShape
import src.com.greg.view.widgets.RectangleShape
import src.com.greg.view.widgets.TextShape

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