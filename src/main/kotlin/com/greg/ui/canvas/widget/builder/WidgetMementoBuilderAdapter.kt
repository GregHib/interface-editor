package com.greg.ui.canvas.widget.builder

import com.greg.ui.canvas.widget.memento.mementoes.Memento
import com.greg.ui.canvas.widget.memento.mementoes.TypeMemento
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup

class WidgetMementoBuilderAdapter(private val memento: Memento? = null) : WidgetBuilder(if (memento is TypeMemento) WidgetType.forString(memento.type) else null) {

    override fun build(id: Int): WidgetGroup {
        val widget = super.build(id)
        if(memento != null)
            widget.restore(memento)
        return widget
    }

    fun build(): WidgetGroup {
        val widget = super.build(-1)
        if(memento != null)
            widget.restore(memento)
        return widget
    }
}