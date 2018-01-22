package com.greg.model.widgets.memento

import com.greg.model.widgets.Widget
import com.greg.model.widgets.WidgetRectangle
import com.greg.model.widgets.WidgetText

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
            memento.add(widget.text)
            memento.add(widget.colour)
        }

        return memento
    }
}