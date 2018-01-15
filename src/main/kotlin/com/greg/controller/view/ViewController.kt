package com.greg.controller.view

import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import tornadofx.View
import tornadofx.borderpane

class ViewController : View() {

    private val canvasView: CanvasView by inject()
    private val toolbarView: ToolbarView by inject()
    private val panelView: PanelView by inject()
    private val hierarchyView: HierarchyView by inject()

    override val root = borderpane {
        top = toolbarView.root
        center = canvasView.root
        right = panelView.root
        left = hierarchyView.root
    }

    init {
        title = "Greg's Interface Editor"
        primaryStage.isResizable = false
        primaryStage.sizeToScene()

        root.setOnKeyPressed { event -> handleKeyPress(event) }
        root.setOnKeyReleased { event -> handleKeyRelease(event) }
        canvasView.root.addEventFilter<MouseEvent>(MouseEvent.ANY, { e -> handleMouseEvent(e) })
    }

    // Keyboard events
    // ------------------------------------------------------------------------------
    private fun handleKeyPress(event: KeyEvent) {
        canvasView.handleKeyPress(event)
        panelView.handleKeyPress(event)
        hierarchyView.handleKeyPress(event)
    }

    private fun handleKeyRelease(event: KeyEvent) {
        canvasView.handleKeyRelease(event)
        panelView.handleKeyRelease(event)
        hierarchyView.handleKeyRelease(event)
    }

    // Mouse events
    // ------------------------------------------------------------------------------
    private fun handleMouseEvent(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> canvasView.handleMousePress(event)
            MouseEvent.MOUSE_DRAGGED -> canvasView.handleMouseDrag(event)
            MouseEvent.MOUSE_RELEASED -> canvasView.handleMouseRelease(event)
            MouseEvent.MOUSE_CLICKED -> if (event.clickCount == 2) canvasView.handleDoubleClick(event) else canvasView.handleMouseClick(event)
        }
    }
}
