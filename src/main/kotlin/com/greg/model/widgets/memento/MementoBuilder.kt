package com.greg.model.widgets.memento

import com.greg.model.widgets.type.Widget
import javafx.beans.property.Property

class MementoBuilder(val widget: Widget) {
    fun build(): Memento {
        val memento = Memento(widget.type)

        widget.properties.get().forEach {
            memento.add(it.property as Property<*>)
        }

        return memento
    }
}