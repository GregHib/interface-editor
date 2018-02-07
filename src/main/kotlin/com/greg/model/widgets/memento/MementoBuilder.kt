package com.greg.model.widgets.memento

import com.greg.model.widgets.type.Widget

class MementoBuilder(val widget: Widget) {
    fun build(): Memento {
        val memento = Memento(widget.type)

        widget.properties.get().forEach {
            memento.add(it.property)
        }

        return memento
    }
}