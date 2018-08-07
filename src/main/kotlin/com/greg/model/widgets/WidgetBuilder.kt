package com.greg.model.widgets

import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetSprite
import com.greg.model.widgets.type.WidgetRectangle
import com.greg.model.widgets.type.WidgetText

open class WidgetBuilder(val type: WidgetType = WidgetType.WIDGET) {

    companion object {
        var identifier = 0

        fun getId() : Int {
            return identifier++
        }
    }

    open fun build(id: Int = -1): Widget {
        val identifier = if(id != -1) id else getId()
        return when(type) {
            WidgetType.TEXT -> WidgetText(this, identifier)
            WidgetType.RECTANGLE -> WidgetRectangle(this, identifier)
            WidgetType.SPRITE -> WidgetSprite(this, identifier)
            else -> {
                Widget(this, identifier)
            }
        }
    }
}