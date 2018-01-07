package com.greg.ui.canvas

import com.greg.controller.ControllerView
import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.state.StateManager
import javafx.geometry.Bounds
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class Canvas(private var controller: ControllerView) {

    var pane: Pane = controller.widgetCanvas
    var selection = Selection(this, controller.widgets)
    var manager = StateManager(this, controller.widgets)

    init {
        // Mouse events
        // ------------------------------------------------------------------------------
        pane.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })
    }

    private fun handleMouseEvent(e: MouseEvent) {
        when (e.eventType) {
            MouseEvent.MOUSE_PRESSED -> manager.handleMousePress(e)
            MouseEvent.MOUSE_DRAGGED -> manager.handleMouseDrag(e)
            MouseEvent.MOUSE_RELEASED -> manager.handleMouseRelease(e)
            MouseEvent.MOUSE_CLICKED -> if (e.clickCount == 2) manager.handleDoubleClick(e) else manager.handleMouseClick(e)
        }
    }

    fun handleKeyPress(event: KeyEvent) {
        manager.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        manager.handleKeyRelease(event)
    }

    fun refreshSelection() {
        controller.panels.reload()
        controller.hierarchy.reload()
    }

    fun layoutBounds(): Bounds {
        return pane.localToScene(pane.layoutBounds)
    }
}