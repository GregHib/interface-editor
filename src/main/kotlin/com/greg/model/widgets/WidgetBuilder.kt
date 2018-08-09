package com.greg.model.widgets

import com.greg.model.widgets.type.*

open class WidgetBuilder(val type: WidgetType = WidgetType.WIDGET) {

    companion object {
        var identifier = 0

        fun getId() : Int {
            return identifier++
        }
    }

    open fun build(id: Int = -1): Widget {
        if(id != -1 && id > identifier)
            identifier = id + 1

        val identifier = if(id != -1) id else getId()
        return when(type) {
            WidgetType.TEXT -> WidgetText(this, identifier)
            WidgetType.RECTANGLE -> WidgetRectangle(this, identifier)
            WidgetType.SPRITE -> WidgetSprite(this, identifier)
            WidgetType.CONTAINER -> WidgetContainer(this, identifier)
            else -> {
                Widget(this, identifier)
            }
        }
    }
}