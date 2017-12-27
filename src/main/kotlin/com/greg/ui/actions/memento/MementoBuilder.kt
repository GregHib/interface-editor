package com.greg.ui.actions.memento

import com.greg.ui.actions.memento.mementoes.Memento
import com.greg.ui.actions.memento.mementoes.TypeMemento
import com.greg.ui.actions.memento.mementoes.WidgetMemento
import com.greg.ui.canvas.widget.builder.data.WidgetData


class MementoBuilder(private val widget: WidgetData? = null) {
    private var name: String? = null

    fun setName(name: String) {
        this.name = name
    }

    fun build(): Memento {
        return if(widget != null) WidgetMemento(widget) else if(name != null) TypeMemento(name!!) else Memento()
    }
}