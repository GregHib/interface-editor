package com.greg.canvas

import com.greg.canvas.selection.SelectionGroup
import com.greg.canvas.state.SelectionController
import com.greg.controller.Controller
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetCanvas {

    var canvasPane: Pane
    private var selectionControl = SelectionController(this)
    var selectionGroup = SelectionGroup(this)
    private var controller: Controller

    constructor(controller: Controller) {
        this.controller = controller
        this.canvasPane = controller.widgetCanvas
        // Mouse events
        // ------------------------------------------------------------------------------
        controller.widgetCanvas.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })
    }

    private fun handleMouseEvent(e: MouseEvent) {
        when (e.eventType) {
            MouseEvent.MOUSE_PRESSED -> selectionControl.handleMousePress(e)
            MouseEvent.MOUSE_DRAGGED -> selectionControl.handleMouseDrag(e)
            MouseEvent.MOUSE_RELEASED -> selectionControl.handleMouseRelease(e)
            MouseEvent.MOUSE_CLICKED -> if (e.clickCount == 2) selectionControl.handleDoubleClick(e) else selectionControl.handleMouseClick(e)
        }
    }

    fun refreshSelection() {
        controller.properties.refresh()
    }
}