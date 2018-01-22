package src.com.greg.controller.widgets

import src.com.greg.model.widgets.Widget
import src.com.greg.model.widgets.WidgetBuilder
import src.com.greg.model.widgets.memento.Memento

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
