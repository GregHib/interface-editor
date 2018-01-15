package com.greg.controller.model

import com.greg.controller.view.WidgetShape

class WidgetsModel {
    protected val widgets = mutableListOf<Widget>()

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun remove(widget: Widget) {
        widgets.remove(widget)
    }

    inline fun forEach(action: (Widget) -> Unit) {
        for (element in widgets)
            action(element)
    }

    fun get(shape: WidgetShape): Widget? {
        forEach { widget ->
            if(widget.identifier == shape.identifier)
                return widget
        }
        return null
    }

    fun get(): MutableList<Widget> {
        return widgets
    }

    fun size(): Int {
        return widgets.size
    }

}