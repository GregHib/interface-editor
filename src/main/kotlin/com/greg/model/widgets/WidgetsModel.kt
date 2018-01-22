package com.greg.model.widgets

import javafx.collections.ObservableList
import com.greg.view.WidgetShape
import tornadofx.observable

open class WidgetsModel {
    val widgets = mutableListOf<Widget>().observable()

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun remove(widget: Widget) {
        println("Remove")
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