package com.greg.ui.panel.panels.attribute

import com.greg.ui.canvas.widget.Widget
import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import javafx.beans.value.WritableValue
import kotlin.reflect.KCallable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class Attribute(val title: String, private val name: String, val type: AttributeType) {

    private lateinit var reflection: KCallable<Widget>
    private val function: Boolean = name.endsWith("Property")

    fun init(facade: WidgetFacade) {
        reflection = get(name, facade, function) as KCallable<Widget>
        reflection.isAccessible = true
    }

    private fun get(name: String, facade: WidgetFacade, function: Boolean): KCallable<*> {
        return if(function)
            facade.getNode()::class.memberFunctions.first { it.name == name }
        else
            facade.getNode()::class.memberProperties.first { it.name == name }
    }

    fun getValue(widget: Widget): Any {
        return if(function) (reflection.call(widget.getNode()) as WritableValue<*>).value else (reflection as KProperty).getter.call(widget.getNode())
    }

    fun isProperty(): Boolean {
        return function
    }

    fun getProperty(widget: Widget): Any {
        return if(function) (reflection.call(widget.getNode()) as WritableValue<*>) else (reflection.call(widget.getNode()) as WritableValue<*>)//Unknown - no support for non-functions/non-properties
    }

    fun setValue(widget: Widget, value: Any?) {
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