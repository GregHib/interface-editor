package com.greg.controller.view

import com.greg.controller.controller.WidgetsController
import com.greg.controller.controller.hierarchy.HierarchyController
import com.greg.controller.controller.input.KeyboardController
import com.greg.ui.hierarchy.CustomTreeCell
import com.greg.ui.hierarchy.CustomTreeItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.util.Callback
import tornadofx.View
import tornadofx.action
import tornadofx.contextmenu
import tornadofx.item

class HierarchyView : View(), KeyboardController {
    private val widgets: WidgetsController by inject()
    private val hierarchy: HierarchyController by inject()
    override val root : StackPane by fxml("/hierarchy.fxml")
    private val tree: TreeView<String> by fxid("hierarchyTree")
    var ignoreRefresh = false
    var ignoreListener = false

    init {
        tree.root = TreeItem("Canvas")
        //Custom name edit not currently supported
        tree.isEditable = true
        //Stick with single selection for now
        tree.selectionModel.selectionMode = SelectionMode.MULTIPLE

        tree.cellFactory = Callback<TreeView<String>, TreeCell<String>> { CustomTreeCell() }
        tree.root.isExpanded = true
        tree.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if(ignoreListener)
                return@addListener

            ignoreRefresh = true

            //Reload canvas selections
            widgets.clearSelection()
            if (newValue != null)
                for (child in tree.selectionModel.selectedItems)
                    (child as? CustomTreeItem)?.widget?.setSelected(true)

            ignoreRefresh = false
        }

        with(tree) {
            contextmenu {
                item("Rename").action {
                    setOnAction { tree.edit(tree.selectionModel.selectedItem) }
                }
                item("Cut").action {
//                    widgets.cut(pane)
                }
                item("Copy").action {
                    widgets.copy()
                }
                item("Delete").action {
//                    widgets.deleteSelection(pane)
                }
            }
        }
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