package com.greg.ui.canvas.state

import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.state.states.CanvasState
import com.greg.ui.canvas.state.states.edit.EditState
import com.greg.ui.canvas.state.states.normal.DefaultState
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

class StateManager(private val canvas: Canvas, private val widgets: Widgets) {

    private var controller: CanvasState = DefaultState(canvas, widgets)

    fun select() {
        controller = DefaultState(canvas, widgets)
    }

    fun edit(widget: WidgetGroup?) {
        if (widget != null)
            controller = EditState(canvas, widget)
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

    fun isEdit(): Boolean {
        return controller is EditState
    }

}