package com.greg.controller.selection

import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.widgets.WidgetsController
import javafx.geometry.BoundingBox
import javafx.scene.input.MouseEvent

class SelectionController(val widgets: WidgetsController, private var canvas: PannableCanvas) {

    fun start(event: MouseEvent) {
        val widget = widgets.getAllIntersections(canvas, BoundingBox(event.x, event.y, 1.0, 1.0)).lastOrNull()

        //If clicked something other than a widget
        var selected = widget == null

        //or clicked a shape which isn't selected
        if (widget != null && !selected)
            selected = !widget.isSelected()

        //Clear current selection
        if (selected && !(event.isShiftDown || event.isControlDown))
            widgets.clearSelection()

        //Always toggle the shape clicked
        if (widget != null) {
            if(event.isControlDown) {
                widget.setSelected(!widget.isSelected())
            } else {
                widget.setSelected(true)
            }
        }
    }

}