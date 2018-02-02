package com.greg.view.canvas

import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

interface CanvasState {

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)

    fun handleKeyPress(event: KeyEvent)

    fun handleKeyRelease(event: KeyEvent)

    fun onClose()
}