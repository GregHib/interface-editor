package com.greg.canvas.widget

import javafx.scene.Node

class WidgetBuilder {

    val components = mutableListOf<Node>()

    init {
        addRectangle()
    }

    fun build(): Widget {
        return Widget(this)
    }

    private fun addRectangle() {
        components.add(WidgetRectangle())
    }

    fun addText() {
        components.add(WidgetText())
    }
}