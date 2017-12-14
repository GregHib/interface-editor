package com.greg.canvas

import com.greg.canvas.selection.SelectionGroup
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.SelectionController
import com.greg.controller.Controller
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetCanvas(private var controller: Controller) {

    var canvasPane: Pane = controller.widgetCanvas
    var selectionControl: PaneController = SelectionController(this)
    var selectionGroup = SelectionGroup(this)

    init {
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
        controller.attributes.reload()
    }

    fun refreshPosition() {
        controller.attributes.refresh()
    }
}