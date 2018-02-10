package com.greg.model.widgets

import com.greg.model.widgets.type.*

enum class WidgetType(val type: String?, val resizable: Boolean = true, val hidden: Boolean = false) {
    WIDGET(Widget::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetText::class.simpleName),
    SPRITE(WidgetSprite::class.simpleName, false),
    CACHE_SPRITE(WidgetCacheSprite::class.simpleName, false),
    ;

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
    }
}