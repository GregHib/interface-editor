package com.greg.view

import javafx.application.Application
import javafx.scene.control.TreeItem
import tornadofx.App
import tornadofx.View
import tornadofx.treeview
import tornadofx.vbox

class HierarchyTreeTest : View() {

    override val root = vbox {

        treeview<String> {
            // Create root item
            root = TreeItem("Root")
            val sub = TreeItem("Test 3")
            sub.children.addAll(TreeItem("Sub Test"))
            root.children.addAll(
                    TreeItem("Test"),
                    TreeItem("Test 2"),
                    sub,
                    TreeItem("Test 4")
            )
        }
    }

}

class HierarchyTreeTestApp: App(HierarchyTreeTest::class)

fun main(args: Array<String>) {
    Application.launch(HierarchyTreeTestApp::class.java, *args)
}