package com.greg.controller.view

import com.greg.controller.controller.hierarchy.HierarchyController
import com.greg.controller.controller.input.KeyboardController
import com.greg.ui.hierarchy.CustomTreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import tornadofx.DefaultScope
import tornadofx.Scope
import tornadofx.View

class HierarchyView : View(), KeyboardController {
    override val root : StackPane by fxml("/hierarchy.fxml")
    private val tree: TreeView<String> by fxid("hierarchyTree")
    private val hierarchy: HierarchyController by inject(DefaultScope, mapOf(Pair("Model", "Works")))

    init {
        hierarchy.tree = tree
    }

    private inline fun forItems(action: (CustomTreeItem) -> Unit) {
        tree.root.children
                .filterIsInstance<CustomTreeItem>()
                .forEach { action(it) }
    }

    override fun handleKeyPress(event: KeyEvent) {
    }

    override fun handleKeyRelease(event: KeyEvent) {
    }
}
class HierarchyScope : Scope() {
    val model = "Model"
}