package com.greg.controller.view

import com.greg.controller.controller.input.KeyboardController
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import tornadofx.View

class PanelView : View(), KeyboardController {
    override val root : StackPane by fxml("/panels.fxml")

    override fun handleKeyPress(event: KeyEvent) {
    }

    override fun handleKeyRelease(event: KeyEvent) {
    }
}