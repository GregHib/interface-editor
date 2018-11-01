package com.greg.model.widgets

import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import com.greg.view.canvas.widgets.WidgetShape
import javafx.collections.ObservableList
import tornadofx.move
import tornadofx.observable

open class WidgetsList {
    val widgets = mutableListOf<Widget>().observable()

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun add(index: Int, widget: Widget) {
        widgets.add(index, widget)
    }

    fun addAll(index: Int, widgets: Collection<Widget>) {
        this.widgets.addAll(index, widgets)
    }

    fun addAll(vararg widgets: Widget) {
        this.widgets.addAll(widgets)
    }

    fun remove(widget: Widget): Boolean {
        return if(widgets.contains(widget))
            widgets.remove(widget)
        else {
            var removed = false//TODO always return true because something could've been removed?
            widgets.forEach { removed = removed or !removeChild(it, widget) }
            !removed
        }
    }

    private fun removeChild(parent: Widget, toRemove: Widget): Boolean {
        if(parent is WidgetContainer) {
            return if(parent.getChildren().contains(toRemove))
                parent.getChildren().remove(toRemove)
            else {
                var removed = false//TODO always return true because something could've been removed?
                parent.getChildren().forEach { removed = removed or !removeChild(it, toRemove) }
                !removed
            }
        }
        return false
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

    fun indexOf(widget: Widget): Int {
        return widgets.indexOf(widget)
    }

    fun move(widget: Widget, index: Int) {
        widgets.move(widget, index)
    }

    inline fun forEach(action: (Widget) -> Unit) {
        widgets.forEach(action)
    }

    fun size(): Int {
        return widgets.size
    }

}