package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import javafx.scene.control.TreeItem

class HierarchyManager(controller: ControllerView) {
    private val tree = controller.hierarchyTree

    init {
        val rootItem = TreeItem("Inbox")
        rootItem.isExpanded = true
        for (i in 1..5) {
            val item = TreeItem("Message" + i)
            rootItem.children.add(item)
        }
        tree.root = rootItem
    }
}