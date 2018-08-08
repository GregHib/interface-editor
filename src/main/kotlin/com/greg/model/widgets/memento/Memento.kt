package com.greg.model.widgets.memento

import com.greg.model.widgets.WidgetType
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.scene.paint.Color

data class Memento(val type: WidgetType, val values: MutableList<String> = mutableListOf()) {
    override fun toString(): String {
        return "$type $values"
    }

    fun add(property: Property<*>) {
        values.add(property.value.toString())
    }

    fun addAll(list: MutableList<String>) {
        values.addAll(list)
    }

    fun getValue(index: Int, property: Property<*>): Any? {
        val value = values[index]

        return when (property) {
            is IntegerProperty -> value.toInt()
            is BooleanProperty -> value.toBoolean()
            is ObjectProperty -> {
                if(value.startsWith("0x"))
                    return Color.valueOf(value)
                else
                    value
            }
            else -> value
        }
    }
}