package com.greg.view

import javafx.application.Application
import javafx.scene.control.TreeItem
import tornadofx.App
import tornadofx.View
import tornadofx.treeview
import tornadofx.vbox
import java.util.regex.Pattern

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

    init {
        var input = "CONTAINER [1, \"one\", false, [CONTAINER [2, \"two\", CONTAINER [3, \"three\"], CONTAINER [false], true], CONTAINER [2, \"string\", false], CONTAINER [4]], 42, true]"//"""CONTAINER [1, "one", false, [CONTAINER [2, "two", false]], 42, true]"""

        input = input.replace(", [", ", (")
        input = input.replace("]], ", "]), ")

        val containers = arrayListOf<String>()

        val arraySplit = input.split("(", ")")

        if(arraySplit.size == 3) {
            val firstContainer = arraySplit[0] + "\$array" + arraySplit[2]
            println("First container $firstContainer")
        }

        var array = arraySplit[1]
        println(array)

        val pattern = Pattern.compile("([A-Z]+\\s\\[[^\\[\\]]+])")

        var index = 0

        while(array.contains("[")) {
            val matcher = pattern.matcher(array)

            while (matcher.find()) {
                containers.add(matcher.group())
                array = array.replace(matcher.group(), "\$${index++}")
            }
        }

        println(array)
        println(containers.reversed())
//        var test2 = "CONTAINER [2, \"two\", CONTAINER [3, \"three\"], CONTAINER [false], true], CONTAINER [2, \"string\", false], CONTAINER [4]]"

//        println(test2.split(" [", ", ", "]").joinToString("\n"))
        /*input.split(" ", "[", ",", "]").filter {
            it != ""
        }.forEach {
            println(it)
        }*/
    }

}

class HierarchyTreeTestApp: App(HierarchyTreeTest::class)

fun main(args: Array<String>) {
    Application.launch(HierarchyTreeTestApp::class.java, *args)
}