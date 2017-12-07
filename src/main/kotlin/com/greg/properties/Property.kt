package com.greg.properties

import com.greg.canvas.widget.Widget
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

class Property {

    val title: String
    val type: PropertyType
    val widgetClass: KClass<out Widget>
    val reflection: KMutableProperty1<out Widget, *>

    constructor(title: String, name: String, type: PropertyType, widget: KClass<out Widget>) {
        this.title = title
        this.type = type
        this.widgetClass = widget
        reflection = Widget.get(name.toLowerCase(), widget)
    }

    override fun toString(): String {
        return "$title $type $widgetClass"
    }
}