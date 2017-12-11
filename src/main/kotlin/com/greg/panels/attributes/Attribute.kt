package com.greg.panels.attributes

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetInterface
import javafx.beans.value.WritableValue
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible

class Attribute(val title: String, name: String, val type: AttributeType, widget: KClass<out WidgetInterface>) {

    val widgetClass: KClass<out WidgetInterface> = widget
    val reflection: KCallable<WidgetInterface>
    private val function: Boolean = name.endsWith("Property")

    override fun toString(): String {
        return "$title $type $widgetClass"
    }

    fun getValue(widget: WidgetInterface): Any {
        return if(function) (reflection.call(widget) as WritableValue<*>).value else (reflection as KProperty).getter.call(widget)
    }

    fun setValue(widget: WidgetInterface, value: Any?) {
        if(function) {
            (reflection.call(widget) as WritableValue<*>).value = value
        } else {
            (reflection as KMutableProperty).setter.call(widget, value)
        }
    }

    init {
        reflection = Widget.get(name, widget as KClass<WidgetInterface>, function) as KCallable<WidgetInterface>
        reflection.isAccessible = true
    }
}