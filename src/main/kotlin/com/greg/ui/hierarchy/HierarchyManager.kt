package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import com.greg.ui.canvas.widget.type.types.WidgetGroup
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
        tree.root.children.add(TreeItem("${widget.identifier} - ${widget.name}"))
    }

    init {
        tree.root = TreeItem("Canvas")
        //Custom name edit not currently supported
//        tree.isEditable = true
        //Stick with single selection for now
//        tree.selectionModel.selectionMode = SelectionMode.MULTIPLE

        tree.cellFactory = Callback<TreeView<String>, TreeCell<String?>> { DragTreeItem(tree, controller) }
        tree.root.isExpanded = true
        tree.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if(ignoreListener)
                return@addListener

            ignoreRefresh = true

            //Reload canvas selections
            controller.canvas.selection.clear()
            if (newValue != null)
                for (widget in controller.widgets.getAll())
                    for (child in tree.selectionModel.selectedItems)
                        if (widget is WidgetGroup && child != null && child.value == "${widget.identifier} - ${widget.name}")
                            controller.canvas.selection.add(widget)

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

        //Clear tree + selections
        tree.root.children.clear()
        tree.selectionModel.clearSelection()

        //Add all widgets and check if selected
        for (widget in controller.widgets.getAll().reversed()) {
            if(widget is WidgetGroup) {
                val item = TreeItem("${widget.identifier} - ${widget.name}")
                tree.root.children.add(item)
                if (controller.canvas.selection.get().contains(widget))
                    tree.selectionModel.select(item)
            }
        }
        ignoreListener = false
    }
}