package com.greg.view.hierarchy

import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.type.WidgetContainer
import com.greg.view.KeyInterface
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.Callback
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class HierarchyView : View(), KeyInterface {

    private val widgets: WidgetsController by inject()
    val rootTreeItem: TreeItem<String> = TreeItem("Root")
    private val tree = TreeView(rootTreeItem)

    fun getExpanded(children: List<TreeItem<String>> = tree.root.children): List<Int> {
        val list = children
                .filterIsInstance<HierarchyItem>()
                .filter { it.widget is WidgetContainer && it.isExpanded }
        val ids = mutableListOf<Int>()
        ids.addAll(list.map { it.widget.identifier })
        ids.addAll(list.flatMap { getExpanded(it.children) })
        return ids
    }

    fun setExpended(ids: List<Int>, children: List<TreeItem<String>> = tree.root.children) {
        children
                .filterIsInstance<HierarchyItem>()
                .filter { it.widget is WidgetContainer }
                .forEach {
                    if(ids.contains(it.widget.identifier))
                        it.isExpanded = true
                    setExpended(ids, it.children)
                }
    }

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