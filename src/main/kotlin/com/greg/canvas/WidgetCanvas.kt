package com.greg.canvas

import com.greg.canvas.properties.PropertyHandler
import com.greg.canvas.selection.SelectionGroup
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.SelectionController
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class WidgetCanvas {

    private var controller: PaneController
    private var selectionGroup = SelectionGroup()
    private var properties: PropertyHandler

    constructor(canvas: Pane) {
        // Mouse events
        // ------------------------------------------------------------------------------
        canvas.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })

        controller = SelectionController(canvas, selectionGroup, refreshSelection())
        properties = PropertyHandler(selectionGroup)
    }

    private fun handleMouseEvent(e: MouseEvent) {
        when (e.eventType) {
            MouseEvent.MOUSE_PRESSED -> controller.handleMousePress(e)
            MouseEvent.MOUSE_DRAGGED -> controller.handleMouseDrag(e)
            MouseEvent.MOUSE_RELEASED -> controller.handleMouseRelease(e)
            MouseEvent.MOUSE_CLICKED -> if (e.clickCount == 2) controller.handleDoubleClick(e) else controller.handleMouseClick(e)
        }
    }

    private fun refreshSelection() {
        properties.refresh()
    }
}