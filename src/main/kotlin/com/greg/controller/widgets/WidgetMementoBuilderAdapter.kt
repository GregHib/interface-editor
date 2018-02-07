package com.greg.controller.widgets

import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.memento.Memento

class WidgetMementoBuilderAdapter(val memento: Memento) : WidgetBuilder(memento.type) {
    override fun build(id: Int): Widget {
        val widget = super.build(id)
        widget.restore(memento)
        return widget
    }

    fun build(): Widget {
        return super.build(-1)
    }
}
