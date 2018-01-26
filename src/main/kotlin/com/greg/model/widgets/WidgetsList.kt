package com.greg.model.widgets

import com.greg.view.WidgetShape
import javafx.collections.ObservableList
import tornadofx.observable

open class WidgetsList {
    val widgets = mutableListOf<Widget>().observable()

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun remove(widget: Widget) {
        widgets.remove(widget)
    }

    inline fun forEach(action: (Widget) -> Unit) {
        widgets.forEach(action)
    }

    fun get(shape: WidgetShape): Widget? {
        forEach { widget ->
            if(widget.identifier == shape.identifier)
                return widget
        }
        return null
    }

    fun get(): ObservableList<Widget> {
        return widgets
    }

    fun size(): Int {
        return widgets.size
    }

}