package com.greg.canvas.widget

import com.greg.canvas.WidgetCanvas

class WidgetBuilder(val canvas: WidgetCanvas) {

    val components = mutableListOf<AttributeWidget>()

    init {
        addRectangle()
    }

    fun build(): Widget {
        return Widget(this).init(canvas)
    }

    private fun addRectangle() {
        components.add(WidgetRectangle())
    }

    fun addText() {
        components.add(WidgetText())
    }
}