package src.com.greg.model.widgets

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