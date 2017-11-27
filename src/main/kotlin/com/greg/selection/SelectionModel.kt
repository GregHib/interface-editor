package com.greg.selection

import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import java.util.*
import kotlin.collections.HashSet

class SelectionModel {
    private var selection: MutableSet<Shape> = HashSet()

    fun add(node: Shape) {
        node.stroke = Color.RED
        selection.add(node)
    }

    fun remove(node: Shape) {
        node.stroke = Color.WHITE
        selection.remove(node)
    }

    fun clear() {
        while (!selection.isEmpty()) {
            remove(selection.iterator().next())
        }
    }

    fun size(): Int {
        return selection.size
    }

    operator fun contains(node: Shape): Boolean {
        return selection.contains(node)
    }

    fun getSelection(): MutableSet<Shape> {
        return selection
    }
}