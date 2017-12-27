package com.greg.ui.actions.memento.mementoes

import com.greg.ui.canvas.widget.builder.data.WidgetData
import com.greg.ui.panel.panels.PanelType

class WidgetMemento(widget: WidgetData) : TypeMemento(widget.components.reversed().first()::class.simpleName.toString()) {

    init {
        for (component in widget.components.reversed()) {
            PanelType.values()
                    .mapNotNull { component.getAttributes(it) }
                    .filter { it.isNotEmpty() }
                    .flatMap { it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title })) }
                    .forEach { values.add(it.getValue(component)) }
        }
    }
}