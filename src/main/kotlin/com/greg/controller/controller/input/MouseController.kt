package com.greg.controller.controller.input

import javafx.scene.input.MouseEvent

interface MouseController {
    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)
}