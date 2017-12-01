package com.greg.canvas.selection

import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.Widget
import javafx.scene.paint.Color

class SelectionGroup(var canvas: WidgetCanvas) : WidgetGroup() {

    fun toggle(widget: Widget) {
        if(contains(widget))
            remove(widget)
        else
            add(widget)
    }

    /*
     * Overrides so only change when needed and no unnecessary refreshes
     */
    override fun add(widget: Widget) {
        if(!contains(widget))
            super.add(widget)
    }

    override fun remove(widget: Widget) {
        if(contains(widget))
            super.remove(widget)
    }

    override fun handleAddition(widget: Widget) {
        widget.setStroke(Color.RED)
        canvas.refreshSelection()
    }

    override fun handleRemoval(widget: Widget) {
        widget.setStroke(Color.WHITE)
        canvas.refreshSelection()
    }
}