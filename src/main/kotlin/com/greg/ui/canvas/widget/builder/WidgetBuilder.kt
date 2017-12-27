package com.greg.ui.canvas.widget.builder

import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import com.greg.ui.canvas.widget.type.types.WidgetText

class WidgetBuilder(type: WidgetType? = WidgetType.WIDGET) {

    val components = mutableListOf<WidgetFacade>()

    init {
        addRectangle()

        if(type == WidgetType.TEXT)
            addText()
    }

    fun build(): WidgetGroup {
        return WidgetGroup(this)
    }

    private fun addRectangle() {
        components.add(WidgetRectangle())
    }

    fun addText() {
        components.add(WidgetText())
    }
}