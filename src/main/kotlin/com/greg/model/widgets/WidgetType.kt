package com.greg.model.widgets

import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetRectangle
import com.greg.model.widgets.type.WidgetSprite
import com.greg.model.widgets.type.WidgetText

enum class WidgetType(val type: String?, val resizable: Boolean) {
    WIDGET(Widget::class.simpleName, true),
    RECTANGLE(WidgetRectangle::class.simpleName, true),
    TEXT(WidgetText::class.simpleName, true),
    SPRITE(WidgetSprite::class.simpleName, false),
    ;

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
    }
}