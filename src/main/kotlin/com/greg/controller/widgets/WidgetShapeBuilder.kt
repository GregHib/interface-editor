package com.greg.controller.widgets

import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.Widget
import com.greg.view.canvas.widgets.*

class WidgetShapeBuilder(val widget: Widget) {
    fun build(): WidgetShape {
        return when(widget.type) {
            WidgetType.CONTAINER -> ContainerShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.MODEL_LIST -> ModelListShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.INVENTORY -> InventoryShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.RECTANGLE -> RectangleShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.TEXT -> TextShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.SPRITE -> SpriteShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.MODEL -> ModelShape(widget.identifier, widget.getWidth(), widget.getHeight())
            WidgetType.ITEM_LIST -> ItemListShape(widget.identifier, widget.getWidth(), widget.getHeight())
        }
    }
}