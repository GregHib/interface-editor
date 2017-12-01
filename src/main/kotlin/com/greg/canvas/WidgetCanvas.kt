package com.greg.canvas

import com.greg.canvas.properties.PropertyHandler
import com.greg.canvas.selection.SelectionGroup
import com.greg.canvas.state.SelectionController
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetCanvas {

    var canvasPane: Pane
    private var controller = SelectionController(this)
    var selectionGroup = SelectionGroup(this)
    private var properties = PropertyHandler(this)

    constructor(canvas: Pane) {
        this.canvasPane = canvas
        // Mouse events
        // ------------------------------------------------------------------------------
        canvas.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })
    }

    private fun handleMouseEvent(e: MouseEvent) {
        when (e.eventType) {
            MouseEvent.MOUSE_PRESSED -> controller.handleMousePress(e)
            MouseEvent.MOUSE_DRAGGED -> controller.handleMouseDrag(e)
            MouseEvent.MOUSE_RELEASED -> controller.handleMouseRelease(e)
            MouseEvent.MOUSE_CLICKED -> if (e.clickCount == 2) controller.handleDoubleClick(e) else controller.handleMouseClick(e)
        }
    }

    fun refreshSelection() {
        properties.refresh()
    }
}