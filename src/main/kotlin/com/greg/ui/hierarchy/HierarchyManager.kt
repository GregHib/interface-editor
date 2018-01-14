package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.util.Callback
import tornadofx.action
import tornadofx.contextmenu
import tornadofx.item

class HierarchyManager(controller: ControllerView) {
    private val tree = controller.hierarchyTree
    private val widgets = controller.widgets
    private val canvas = controller.canvas
    var ignoreRefresh = false
    var ignoreListener = false

    fun add(widget: WidgetGroup) {
        tree.root.children.add(CustomTreeItem(widget))
    }

    fun remove(widget: WidgetGroup) {
        var found = 0
        for((index, child) in tree.root.children.withIndex()) {
            if(child is CustomTreeItem && child.widget.identifier == widget.identifier) {
                found = index
                break
            }
        }
        tree.root.children.removeAt(found)
    }

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
            canvas.selection.clear()
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
                    canvas.selection.copy()
                    canvas.selection.deleteAll()
                }
                item("Copy").action {
                    canvas.selection.copy()
                }
                item("Delete").action {
                    canvas.selection.deleteAll()
                }
            }
        }
    }

    fun reload() {
        if(ignoreRefresh)
            return

        ignoreListener = true

        //Clear selection
        tree.selectionModel.clearSelection()

        //Select all items that are selected on canvas
        widgets.forWidgetsReversed { widget ->
            if (canvas.selection.contains(widget)) {
               forItems items@ {
                    if(it.widget.identifier == widget.identifier) {
                        tree.selectionModel.select(it)
                        return@items
                    }
                }
            }
        }

        ignoreListener = false
    }

    private inline fun forItems(action: (CustomTreeItem) -> Unit) {
        tree.root.children
                .filterIsInstance<CustomTreeItem>()
                .forEach { action(it) }
    }
}