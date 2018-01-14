package com.greg.controller.controller

import com.greg.ui.canvas.widget.type.WidgetType

data class Memento(val type: WidgetType, val values: MutableList<Any> = mutableListOf()) {
    override fun toString(): String {
        return "$type $values"
    }
}