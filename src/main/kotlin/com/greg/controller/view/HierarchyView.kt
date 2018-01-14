package com.greg.controller.view

import com.greg.controller.controller.input.KeyboardController
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import tornadofx.View

class HierarchyView : View(), KeyboardController {
    override val root : StackPane by fxml("/hierarchy.fxml")
    val hierarchyTree: TreeView<String> by fxid()

    override fun handleKeyPress(event: KeyEvent) {
    }

    override fun handleKeyRelease(event: KeyEvent) {
    }
}