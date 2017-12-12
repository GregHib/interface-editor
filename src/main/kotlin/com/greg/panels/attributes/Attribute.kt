package com.greg.panels.attributes

import com.greg.canvas.widget.WidgetInterface
import javafx.beans.value.WritableValue
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class Attribute(val title: String, name: String, val type: AttributeType, widget: KClass<out WidgetInterface>) {

    val widgetClass: KClass<out WidgetInterface> = widget
    private val reflection: KCallable<WidgetInterface>
    private val function: Boolean = name.endsWith("Property")

    init {
        reflection = get(name, widget as KClass<WidgetInterface>, function) as KCallable<WidgetInterface>
        reflection.isAccessible = true
    }

    private fun get(name: String, widget: KClass<WidgetInterface>, function: Boolean): KCallable<*> {
        return if(function)
            widget.memberFunctions.first { it.name == name }
        else
            widget.memberProperties.first { it.name == name }
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

    override fun toString(): String {
        return "$title $type $widgetClass"
    }
}