package com.greg.canvas.state

import javafx.scene.input.MouseEvent

interface PaneController {

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)
}