package com.greg.ui.canvas.selection

import com.greg.ui.canvas.widget.type.types.WidgetGroup

abstract class WidgetSet {
    private var group: MutableSet<WidgetGroup> = HashSet()

    open fun add(widget: WidgetGroup) {
        group.add(widget)
        handleAddition(widget)
    }

    open fun remove(widget: WidgetGroup) {
        group.remove(widget)
        handleRemoval(widget)
    }

    fun clear() {
        while (!group.isEmpty())
            remove(group.iterator().next())
    }

    fun size(): Int {
        return group.size
    }

    operator fun contains(widget: WidgetGroup): Boolean {
        return group.contains(widget)
    }

    fun getGroup(): MutableSet<WidgetGroup> {
        return group
    }

    abstract fun handleAddition(widget: WidgetGroup)

    abstract fun handleRemoval(widget: WidgetGroup)
}