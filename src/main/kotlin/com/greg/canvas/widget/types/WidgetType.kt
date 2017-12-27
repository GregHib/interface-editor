package com.greg.canvas.widget.types

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.types.impl.WidgetRectangle
import com.greg.canvas.widget.types.impl.WidgetText

enum class WidgetType(private val simpleName: kotlin.String?) {
    WIDGET(Widget::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetText::class.simpleName);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { string == it.simpleName }
        }
    }
}