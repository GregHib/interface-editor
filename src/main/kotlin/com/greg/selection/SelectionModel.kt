package com.greg.selection

import com.greg.widget.Widget
import javafx.scene.paint.Color
import kotlin.collections.HashSet

class SelectionModel {
    private var selection: MutableSet<Widget> = HashSet()

    fun add(node: Widget) {
        node.setStroke(Color.RED)
        selection.add(node)
    }

    fun remove(node: Widget) {
        node.setStroke(Color.WHITE)
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

    operator fun contains(node: Widget): Boolean {
        return selection.contains(node)
    }

    fun getSelection(): MutableSet<Widget> {
        return selection
    }
}