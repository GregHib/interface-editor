package com.greg.ui.canvas.selection

import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.state.states.normal.selection.Interaction
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.MouseEvent

class Selection(canvas: Canvas, private val widgets: Widgets) {
    private var group = SelectionGroup(canvas)
    private val interaction = Interaction(this, widgets)

    fun init(event: MouseEvent, widget: WidgetGroup?) {
        //If clicked something other than a widget
        var selected = widget == null

        if (widget != null && !selected) {
            //or clicked a shape which isn't selected
            selected = !group.contains(widget)
        }

        //Clear current selection
        if (selected && !(event.isShiftDown || event.isControlDown))
            clear()

        //Always toggle the shape clicked
        if (widget != null)
            handle(widget, event)
    }

    fun clear() {
        group.clear()
    }

    fun add(widget: WidgetGroup) {
        group.add(widget)
    }

    fun remove(widget: WidgetGroup) {
        group.remove(widget)
    }

    fun get(): MutableSet<WidgetGroup> {
        return group.getGroup()
    }

    fun size(): Int {
        return group.size()
    }

    fun contains(widget: WidgetGroup): Boolean {
        return group.getGroup().contains(widget)
    }

    fun handle(widget: WidgetGroup, event: MouseEvent) {
        if (event.isControlDown) {
            toggle(widget)
        } else {
            add(widget)
        }
    }

    private fun toggle(widget: WidgetGroup) {
        group.toggle(widget)
    }

    /**
     * Commands
     */

    fun paste() {
        interaction.paste()
    }

    fun copy() {
        interaction.copy()
    }

    fun clone() {
        interaction.clone()
    }

    fun selectAll() {
        widgets.getAll().forEach { node ->
            if (node is WidgetGroup && !contains(node))
                add(node)
        }
    }

    fun deleteAll() {
        group.getGroup().forEach { widget ->
            val success = widgets.remove(widget)
            if (!success)
                error("Error deleting widget")
        }
        clear()
    }
}