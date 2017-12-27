package com.greg.canvas.state

import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.edit.EditController
import com.greg.canvas.state.selection.SelectionController
import com.greg.canvas.widget.Widget
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

class ControllerManager(private val canvas: WidgetCanvas) {

    private var controller: PaneController = SelectionController(canvas)

    fun select() {
        controller = SelectionController(canvas)
    }

    fun edit(widget: Widget?) {
        if (widget != null)
            controller = EditController(canvas, widget)
    }

    fun handleMousePress(event: MouseEvent) {
        controller.handleMousePress(event)
    }

    fun handleMouseDrag(event: MouseEvent) {
        controller.handleMouseDrag(event)
    }

    fun handleMouseRelease(event: MouseEvent) {
        controller.handleMouseRelease(event)
    }

    fun handleDoubleClick(event: MouseEvent) {
        controller.handleDoubleClick(event)
    }

    fun handleMouseClick(event: MouseEvent) {
        controller.handleMouseClick(event)
    }

    fun handleKeyPress(event: KeyEvent) {
        controller.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        controller.handleKeyRelease(event)
    }

}