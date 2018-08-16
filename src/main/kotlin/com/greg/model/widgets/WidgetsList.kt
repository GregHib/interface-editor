package com.greg.model.widgets

import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import com.greg.view.canvas.widgets.WidgetShape
import javafx.collections.ObservableList
import tornadofx.observable

open class WidgetsList {
    val widgets = mutableListOf<Widget>().observable()

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun addAll(vararg widget: Widget) {
        widgets.addAll(widget)
    }

    fun remove(widget: Widget) {
        if(widgets.contains(widget))
            widgets.remove(widget)
        else {
            widgets.forEach { removeChild(it, widget) }
        }
    }

    private fun removeChild(parent: Widget, toRemove: Widget) {
        if(parent is WidgetContainer) {
            if(parent.getChildren().contains(toRemove))
                parent.getChildren().remove(toRemove)
            else
                parent.getChildren().forEach { removeChild(it, toRemove) }
        }
    }

    private fun getChildren(widget: Widget): List<Widget> {
        val widgets = arrayListOf<Widget>()
        widgets.add(widget)
        (widget as? WidgetContainer)?.getChildren()?.forEach { widgets.addAll(getChildren(it)) }
        return widgets
    }

    fun get(shape: WidgetShape): Widget? {
        forAll { widget ->
            if(widget.identifier == shape.identifier)
                return widget
        }
        return null
    }

    fun getAll(): List<Widget> {
        val widgets = arrayListOf<Widget>()
        this.widgets.forEach { widgets.addAll(getChildren(it)) }
        return widgets
    }

    inline fun forAll(action: (Widget) -> Unit) {
        getAll().forEach(action)
    }

    fun get(): ObservableList<Widget> {
        return widgets
    }

    inline fun forEach(action: (Widget) -> Unit) {
        widgets.forEach(action)
    }

    fun size(): Int {
        return widgets.size
    }

}