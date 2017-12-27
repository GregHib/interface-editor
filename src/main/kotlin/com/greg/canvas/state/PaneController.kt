package com.greg.canvas.state

import com.greg.canvas.WidgetCanvas
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

interface PaneController {

    val canvas: WidgetCanvas

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)

    fun handleKeyPress(event: KeyEvent)

    fun handleKeyRelease(event: KeyEvent)
}