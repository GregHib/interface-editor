package com.greg.ui.canvas.widget.builder

import com.greg.ui.actions.memento.mementoes.Memento
import com.greg.ui.actions.memento.mementoes.TypeMemento
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup

class WidgetMementoBuilderAdapter(private val memento: Memento? = null) : WidgetBuilder(if (memento is TypeMemento) WidgetType.forString(memento.type) else null) {

    override fun build(): WidgetGroup {
        val widget = super.build()
        if(memento != null)
            widget.restore(memento)
        return widget
    }
}