package com.greg.panels.attributes

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetInterface
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class Attribute {

    val title: String
    val type: AttributeType
    val widgetClass: KClass<out WidgetInterface>
    val reflection: KFunction<out WidgetInterface>

    constructor(title: String, name: String, type: AttributeType, widget: KClass<out WidgetInterface>) {
        this.title = title
        this.type = type
        this.widgetClass = widget
        reflection = Widget.get(name, widget)
    }

    override fun toString(): String {
        return "$title $type $widgetClass"
    }
}