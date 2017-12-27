package com.greg.canvas.widget

import com.greg.canvas.widget.types.impl.WidgetRectangle
import com.greg.canvas.widget.types.impl.WidgetText
import com.greg.canvas.widget.types.WidgetType

class WidgetBuilder(type: WidgetType? = WidgetType.WIDGET) {

    val components = mutableListOf<AttributeWidget>()

    init {
        addRectangle()

        if(type == WidgetType.TEXT)
            addText()
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