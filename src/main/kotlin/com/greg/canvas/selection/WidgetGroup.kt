package com.greg.canvas.selection

import com.greg.canvas.widget.Widget

abstract class WidgetGroup {
    private var group: MutableSet<Widget> = HashSet()

    open fun add(widget: Widget) {
        group.add(widget)
        handleAddition(widget)
    }

    open fun remove(widget: Widget) {
        group.remove(widget)
        handleRemoval(widget)
    }

    fun clear() {
        while (!group.isEmpty()) {
            remove(group.iterator().next())
        }
    }

    fun size(): Int {
        return group.size
    }

    operator fun contains(widget: Widget): Boolean {
        return group.contains(widget)
    }

    fun getGroup(): MutableSet<Widget> {
        return group
    }

    abstract fun handleAddition(widget: Widget)

    abstract fun handleRemoval(widget: Widget)
}