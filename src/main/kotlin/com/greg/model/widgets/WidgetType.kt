package com.greg.model.widgets

enum class WidgetType(val type: String?, val resizable: Boolean) {
    WIDGET(Widget::class.simpleName, true),
    RECTANGLE(WidgetRectangle::class.simpleName, true),
    TEXT(WidgetText::class.simpleName, true);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
    }
}