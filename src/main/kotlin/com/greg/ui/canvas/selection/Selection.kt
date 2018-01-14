package com.greg.ui.canvas.selection

import com.greg.ui.canvas.state.states.normal.selection.Interaction
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.MouseEvent

class Selection(val widgets: Widgets) {
    private val interaction = Interaction(this, widgets)

    fun start(event: MouseEvent, widget: WidgetGroup?) {
        //If clicked something other than a widget
        var selected = widget == null

        if (widget != null && !selected) {
            //or clicked a shape which isn't selected
            selected = !contains(widget)
        }

        //Clear current selection
        if (selected && !(event.isShiftDown || event.isControlDown))
            clear()

        //Always toggle the shape clicked
        if (widget != null)
            handle(widget, event)
    }

    inline fun forSelected(action: (WidgetGroup) -> Unit) {
        widgets.getAll()
                .filterIsInstance<WidgetGroup>()
                .filter { it.isSelected() }
                .forEach { action(it) }
    }

    fun get(): List<WidgetGroup> {
        return widgets.getAll()
                .filterIsInstance<WidgetGroup>()
                .filter { it.isSelected() }
    }

    fun size(): Int {
        return get().size
    }

    fun contains(widget: WidgetGroup): Boolean {
        return widgets.getAll()
                .filterIsInstance<WidgetGroup>()
                .filter { it.isSelected() }
                .contains(widget)
    }

    fun handle(widget: WidgetGroup, event: MouseEvent) {
        if (event.isControlDown) {
            widget.setSelected(!widget.isSelected())
        } else {
            widget.setSelected(true)
        }
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

    fun clear() {
        forSelected { widget ->
            widget.setSelected(false)
        }
    }

    fun selectAll() {
        widgets.forWidgets { widget ->
            if(!widget.isSelected())
                widget.setSelected(true)
        }
    }

    fun deleteAll() {
        forSelected { widget ->
            widgets.remove(widget)
        }
        clear()
    }
}