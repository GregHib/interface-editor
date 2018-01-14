package com.greg.controller.controller.input

import javafx.scene.input.KeyEvent

interface KeyboardController {
    fun handleKeyPress(event: KeyEvent)

    fun handleKeyRelease(event: KeyEvent)
}