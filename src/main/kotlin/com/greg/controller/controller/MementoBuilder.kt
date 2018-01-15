package com.greg.controller.controller

import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetRectangle
import com.greg.controller.model.WidgetText

class MementoBuilder(val widget: Widget) {
    fun build(): Memento {
        val memento = Memento(widget.type)

        memento.add(widget.xProperty())
        memento.add(widget.yProperty())
        memento.add(widget.widthProperty())
        memento.add(widget.heightProperty())


        if (widget is WidgetRectangle) {
            memento.add(widget.fillProperty())
            memento.add(widget.strokeProperty())
        } else if (widget is WidgetText) {
            memento.add(widget.textProperty())
            memento.add(widget.colourProperty())
        }

        return memento
    }
}