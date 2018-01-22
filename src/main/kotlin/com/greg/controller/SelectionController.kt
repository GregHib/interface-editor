package com.greg.controller

import javafx.scene.input.MouseEvent
import com.greg.controller.widgets.WidgetsController

class SelectionController(val widgets: WidgetsController) {

    fun start(event: MouseEvent) {
        val widget = widgets.getWidget(event.target)

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