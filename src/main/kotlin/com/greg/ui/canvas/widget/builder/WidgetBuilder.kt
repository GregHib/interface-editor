package com.greg.ui.canvas.widget.builder

import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import com.greg.ui.canvas.widget.type.types.WidgetText

open class WidgetBuilder(type: WidgetType? = WidgetType.WIDGET) {

    companion object {
        var identifier = 0
        fun getId() : Int {
            return identifier++
        }
    }
    val components = mutableListOf<WidgetFacade>()

    init {
        addRectangle()

        if(type == WidgetType.TEXT)
            addText()
    }

    open fun build(id: Int = -1): WidgetGroup {
        return WidgetGroup(this, if(id != -1) id else getId())
    }

    private fun addRectangle() {
        components.add(WidgetRectangle())
    }

    fun addText() {
        components.add(WidgetText())
    }
}