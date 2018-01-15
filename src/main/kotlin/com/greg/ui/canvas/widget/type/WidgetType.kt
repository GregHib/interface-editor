package com.greg.ui.canvas.widget.type

import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetRectangle
import com.greg.controller.model.WidgetText

enum class WidgetType(val type: String?) {
    WIDGET(Widget::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetText::class.simpleName);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
    }
}