package com.greg.canvas.state

import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

interface PaneController {
    var canvas: Pane

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)
}