package com.greg.controller.model

import com.greg.controller.controller.Memento

class WidgetMementoBuilderAdapter(val memento: Memento) : WidgetBuilder(memento.type) {
    override fun build(id: Int): Widget {
        val widget = super.build(id)
        widget.restore(memento)
        return widget
    }

    fun build(): Widget {
        val widget = super.build(-1)
        widget.restore(memento)
        return widget
    }
}
