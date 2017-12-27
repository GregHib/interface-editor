package com.greg.canvas.state.selection

import com.greg.canvas.widget.Widget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetSelection(private val selectionGroup: SelectionGroup, private val canvasPane: Pane) {

    private val interaction = InteractionController(selectionGroup, canvasPane)

    fun init(event: MouseEvent, widget: Widget?) {
        //If clicked something other than a widget
        var selected = widget == null

        if (widget != null && !selected) {
            //or clicked a shape which isn't selected
            selected = !selectionGroup.contains(widget)
        }

        //Clear current selection
        if (selected && !isMultiSelect(event))
            clear()

        //Always toggle the shape clicked
        if (widget != null)
            handle(widget, event)
    }

    fun delete() {
        selectionGroup.getGroup().forEach { widget ->
            val success = canvasPane.children.remove(widget)
            if (!success)
                println("Error deleting widget")
        }
        clear()
    }

    fun clear() {
        selectionGroup.clear()
    }

    fun add(widget: Widget) {
        selectionGroup.add(widget)
    }

    fun remove(widget: Widget) {
        selectionGroup.remove(widget)
    }

    fun contains(widget: Widget): Boolean {
        return selectionGroup.getGroup().contains(widget)
    }

    fun toggle(widget: Widget) {
        selectionGroup.toggle(widget)
    }

    fun handle(widget: Widget, event: MouseEvent) {
        if (event.isControlDown) {
            toggle(widget)
        } else {
            add(widget)
        }
    }

    /**
     * Clipboard
     */

    fun paste() {
        interaction.paste()
    }

    fun copy() {
        interaction.copy()
    }

    private fun isMultiSelect(event: MouseEvent): Boolean {
        return event.isShiftDown || event.isControlDown
    }

    fun clone() {
        interaction.clone()
    }

    fun selectAll() {
        canvasPane.children.forEach { node ->
            if (node is Widget && !contains(node))
                add(node)
        }
    }
}