package com.greg.canvas.widget.types

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.types.impl.WidgetRectangle

enum class WidgetType(private val simpleName: kotlin.String?) {
    WIDGET(Widget::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetRectangle::class.simpleName);

    companion object {
        fun forString(string: String): WidgetType? {
            return values().firstOrNull { string == it.simpleName }
        }
    }
}