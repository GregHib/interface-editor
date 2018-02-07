package com.greg.view.hierarchy

import com.greg.controller.widgets.WidgetsController
import com.greg.model.Sprite
import com.greg.view.KeyInterface
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.Callback
import org.controlsfx.control.CheckTreeView
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class HierarchyView : View(), KeyInterface {

    private val widgets: WidgetsController by inject()
    val rootTreeItem = CheckBoxTreeItem("Root")
    private val tree = CheckTreeView(rootTreeItem)

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab("Hierarchy") {

            rootTreeItem.isExpanded = true

            tree.selectionModel.selectionMode = SelectionMode.MULTIPLE
            tree.cellFactory = Callback<TreeView<String>, TreeCell<String>> {
                DragTreeCell()
            }

            add(tree)
        }
    }


    override fun handleKeyEvents(event: KeyEvent) {
        if(tree.isFocused) {
            when (event.eventType) {
                KeyEvent.KEY_RELEASED -> {
                    if (event.code == KeyCode.DELETE) {
                        val iterator = tree.selectionModel.selectedItems.filterIsInstance<HierarchyItem>().iterator()
                        while(iterator.hasNext()) {
                            val next = iterator.next()
                            widgets.delete(next.identifier)
                        }
                    }
                }
            }

            event.consume()
        }
    }
}