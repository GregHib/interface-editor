package com.greg.canvas.selection

import com.greg.selection.WidgetGroup
import com.greg.widget.Widget
import javafx.scene.paint.Color

class SelectionGroup : WidgetGroup() {

    fun toggle(widget: Widget) {
        if(contains(widget))
            remove(widget)
        else
            add(widget)
    }


    override fun handleAddition(widget: Widget) {
        widget.setStroke(Color.RED)
    }

    override fun handleRemoval(widget: Widget) {
        widget.setStroke(Color.WHITE)
    }
}