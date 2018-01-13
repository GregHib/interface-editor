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

class HierarchyManager(private val controller: ControllerView) {
    private val tree = controller.hierarchyTree
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

        tree.cellFactory = Callback<TreeView<String>, TreeCell<String>> { CustomTreeCell(tree, controller) }
        tree.root.isExpanded = true
        tree.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if(ignoreListener)
                return@addListener

            ignoreRefresh = true

            //Reload canvas selections
            controller.canvas.selection.clear()
            if (newValue != null)
                for (widget in controller.widgets.getAll())
                    for (child in tree.selectionModel.selectedItems) {
                        if (widget is WidgetGroup && child is CustomTreeItem && child.widget.identifier == widget.identifier && !child.widget.locked)
                            controller.canvas.selection.add(widget)
                    }

            ignoreRefresh = false
        }

        with(tree) {
            contextmenu {
                item("Rename").action {
                    setOnAction { tree.edit(tree.selectionModel.selectedItem) }
                }
                item("Cut").action {
                    controller.canvas.selection.copy()
                    controller.canvas.selection.deleteAll()
                }
                item("Copy").action {
                    controller.canvas.selection.copy()
                }
                item("Delete").action {
                    controller.canvas.selection.deleteAll()
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
        controller.widgets.forWidgetsReversed { widget ->
            if (controller.canvas.selection.get().contains(widget)) {
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