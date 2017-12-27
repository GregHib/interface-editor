package com.greg.ui.canvas.widget.type

import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import com.greg.ui.canvas.widget.type.types.WidgetText

enum class WidgetType(private val simpleName: kotlin.String?) {
    WIDGET(WidgetGroup::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetText::class.simpleName);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { string == it.simpleName }
        }
    }
}