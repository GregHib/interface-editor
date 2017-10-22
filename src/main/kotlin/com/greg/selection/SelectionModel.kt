package com.greg.selection

import javafx.scene.Node
import java.util.*

class SelectionModel {
    private var selection: MutableSet<Node> = HashSet()

    fun add(node: Node) {
        node.style = "-fx-effect: dropshadow(three-pass-box, red, 2, 2, 0, 0);"
        selection.add(node)
    }

    fun remove(node: Node) {
        node.style = "-fx-effect: null"
        selection.remove(node)
    }

    fun clear() {
        while (!selection.isEmpty()) {
            remove(selection.iterator().next())
        }
    }

    operator fun contains(node: Node): Boolean {
        return selection.contains(node)
    }

    fun log() {
        println("Items in model: " + Arrays.asList<Any>(*selection.toTypedArray()))
    }
}