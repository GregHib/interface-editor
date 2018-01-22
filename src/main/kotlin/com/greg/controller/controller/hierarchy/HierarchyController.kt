package com.greg.controller.controller.hierarchy

import com.greg.controller.controller.WidgetsController
import com.greg.controller.model.Widget
import com.greg.ui.hierarchy.CustomTreeCell
import com.greg.ui.hierarchy.CustomTreeItem
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.util.Callback
import tornadofx.Controller
import tornadofx.action
import tornadofx.contextmenu
import tornadofx.item

class HierarchyController : Controller() {
    init {
        println(params)
    }
    private val widgets: WidgetsController by inject()
    lateinit var tree: TreeView<String>
    var ignoreRefresh = false
    var ignoreListener = false

    fun add(widget: Widget) {
//        tree.root.children.add(CustomTreeItem(widget))
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
            if (ignoreListener)
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
                    widgets.cut()
                }
                item("Copy").action {
                    widgets.copy()
                }
                item("Delete").action {
                    widgets.deleteSelection()
                }
            }
        }
    }

    fun reload() {

    }
}