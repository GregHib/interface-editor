package com.greg.canvas.widget

class WidgetBuilder {

    val components = mutableListOf<AttributeWidget>()

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