package com.greg.properties.attributes

import com.greg.canvas.widget.Widget
import com.greg.properties.PropertyType
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

class Property {

    val title: String
    val type: PropertyType
    val reflection: KMutableProperty1<out Widget, *>

    constructor(title: String, name: String, type: PropertyType, widget: KClass<out Widget>) {
        this.title = title
        this.type = type
        reflection = Widget.get(name.toLowerCase(), widget)
    }
}