package com.greg.canvas.widget

import com.greg.canvas.WidgetCanvas

class WidgetBuilder {

    val canvas: WidgetCanvas

    constructor(canvas: WidgetCanvas) {
        this.canvas = canvas
    }

    constructor(canvas: WidgetCanvas, typeName: String) {
        this.canvas = canvas

        if (typeName == WidgetText::class.simpleName)
            addText()
    }

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