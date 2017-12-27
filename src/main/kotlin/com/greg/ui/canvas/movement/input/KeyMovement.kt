package com.greg.ui.canvas.movement.input

import com.greg.ui.canvas.movement.MovementProxy
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class KeyMovement(private val movement: MovementProxy) {

    private var horizontal = 0.0
    private var vertical = 0.0

    fun move(event: KeyEvent) {
        when {
            event.code == KeyCode.RIGHT -> horizontal = 1.0
            event.code == KeyCode.LEFT -> horizontal = -1.0
            event.code == KeyCode.UP -> vertical = -1.0
            event.code == KeyCode.DOWN -> vertical = 1.0
        }

        movement.move(if (event.isShiftDown) horizontal * 10.0 else horizontal, if (event.isShiftDown) vertical * 10.0 else vertical)
    }

    fun reset(code: KeyCode) {
        if(code == KeyCode.RIGHT || code == KeyCode.LEFT)
            horizontal = 0.0

        if(code == KeyCode.UP || code == KeyCode.DOWN)
            vertical = 0.0
    }
}