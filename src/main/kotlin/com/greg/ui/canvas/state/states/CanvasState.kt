package com.greg.ui.canvas.state.states

import com.greg.ui.canvas.Canvas
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

interface CanvasState {

    val canvas: Canvas

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)

    fun handleKeyPress(event: KeyEvent)

    fun handleKeyRelease(event: KeyEvent)
}