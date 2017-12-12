package com.greg.canvas.widget

import javafx.scene.Node

class WidgetBuilder {

    var components = mutableListOf<Node>()

    fun build(): Widget {
        return Widget(this)
    }

    fun addRectangle() {
        components.add(WidgetRectangle())
    }

    fun addText() {
        components.add(WidgetText())
    }
}