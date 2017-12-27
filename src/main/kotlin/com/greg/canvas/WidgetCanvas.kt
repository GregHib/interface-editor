package com.greg.canvas

import com.greg.canvas.state.ControllerManager
import com.greg.canvas.state.selection.SelectionGroup
import com.greg.controller.AppController
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetCanvas(private var appController: AppController) {

    var canvasPane: Pane = appController.widgetCanvas
    var selectionGroup = SelectionGroup(this)
    var controller = ControllerManager(this)

    init {
        // Mouse events
        // ------------------------------------------------------------------------------
        canvasPane.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })
    }

    private fun handleMouseEvent(e: MouseEvent) {
        when (e.eventType) {
            MouseEvent.MOUSE_PRESSED -> controller.handleMousePress(e)
            MouseEvent.MOUSE_DRAGGED -> controller.handleMouseDrag(e)
            MouseEvent.MOUSE_RELEASED -> controller.handleMouseRelease(e)
            MouseEvent.MOUSE_CLICKED -> if (e.clickCount == 2) controller.handleDoubleClick(e) else controller.handleMouseClick(e)
        }
    }

    fun handleKeyPress(event: KeyEvent) {
        controller.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        controller.handleKeyRelease(event)
    }

    fun refreshSelection() {
        appController.attributes.reload()
    }
}