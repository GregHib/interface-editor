package com.greg.properties

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetInterface
import javafx.beans.value.WritableValue
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class Property {

    val title: String
    val thing: KClass<out WritableValue<*>>
    val type: PropertyType
    val widgetClass: KClass<out WidgetInterface>
    val reflection: KFunction<out WidgetInterface>

    constructor(title: String, name: String, thing: KClass<out WritableValue<*>>, type: PropertyType, widget: KClass<out WidgetInterface>) {
        this.title = title
        this.type = type
        this.thing = thing
        this.widgetClass = widget
        reflection = Widget.get(name, widget)
    }

    override fun toString(): String {
        return "$title $type $widgetClass"
    }
}