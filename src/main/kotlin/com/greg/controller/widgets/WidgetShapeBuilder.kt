package com.greg.controller.widgets

import com.greg.model.widgets.type.Widget
import com.greg.view.canvas.widgets.WidgetShape

class WidgetShapeBuilder(val widget: Widget) {
    fun build(): WidgetShape {
        return widget.type.shape.constructors.first().call(widget.identifier, widget.getWidth(), widget.getHeight())
    }
}