package com.greg.canvas.selection

import com.greg.canvas.widget.Widget

abstract class WidgetGroup {
    private var group: MutableSet<Widget> = HashSet()

    open fun add(node: Widget) {
        group.add(node)
        handleAddition(node)
    }

    fun remove(node: Widget) {
        group.remove(node)
        handleRemoval(node)
    }

    fun clear() {
        while (!group.isEmpty()) {
            remove(group.iterator().next())
        }
    }

    fun size(): Int {
        return group.size
    }

    operator fun contains(node: Widget): Boolean {
        return group.contains(node)
    }

    fun getGroup(): MutableSet<Widget> {
        return group
    }

    abstract fun handleAddition(widget: Widget)

    abstract fun handleRemoval(widget: Widget)
}