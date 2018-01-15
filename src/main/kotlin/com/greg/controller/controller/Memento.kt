package com.greg.controller.controller

import com.greg.ui.canvas.widget.type.WidgetType
import javafx.beans.property.Property

data class Memento(val type: WidgetType, val values: MutableList<MementoValue> = mutableListOf()) {
    override fun toString(): String {
        return "$type $values"
    }

    fun add(property: Property<*>) {
        values.add(MementoValue(property.value.toString()))
    }

    fun addAll(list: MutableList<String>) {
        list.mapTo(values) { MementoValue(it) }
    }
}