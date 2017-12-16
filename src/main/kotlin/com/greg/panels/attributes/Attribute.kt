package com.greg.panels.attributes

import com.greg.canvas.widget.AttributeWidget
import com.greg.canvas.widget.WidgetInterface
import javafx.beans.value.WritableValue
import kotlin.reflect.KCallable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class Attribute(val title: String, private val name: String, val type: AttributeType) {
    private lateinit var reflection: KCallable<WidgetInterface>
    private val function: Boolean = name.endsWith("Property")

    fun init(widget: AttributeWidget) {
        reflection = get(name, widget, function) as KCallable<WidgetInterface>
        reflection.isAccessible = true
    }

    private fun get(name: String, widget: AttributeWidget, function: Boolean): KCallable<*> {
        return if(function)
            widget.getNode()::class.memberFunctions.first { it.name == name }
        else
            widget.getNode()::class.memberProperties.first { it.name == name }
    }

    fun getValue(widget: WidgetInterface): Any {
        return if(function) (reflection.call(widget.getNode()) as WritableValue<*>).value else (reflection as KProperty).getter.call(widget.getNode())
    }

    fun isProperty(): Boolean {
        return function
    }

    fun getProperty(widget: WidgetInterface): Any {
        return if(function) (reflection.call(widget.getNode()) as WritableValue<*>) else (reflection.call(widget.getNode()) as WritableValue<*>)//Unknown - no support for non-functions/non-properties
    }

    fun setValue(widget: WidgetInterface, value: Any?) {
        if(function) {
            (reflection.call(widget.getNode()) as WritableValue<*>).value = value
        } else {
            (reflection as KMutableProperty).setter.call(widget.getNode(), value)
        }
    }

    override fun toString(): String {
        return "$title $name $type"
    }
}