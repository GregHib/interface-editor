package com.greg.model

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType

class WidgetTypeInstance {
    init {
        val type = WidgetType.RECTANGLE

        type.widget.constructors.forEach {
            println("Constructor $it")
            println("Call ${it.call(WidgetBuilder(type), 0)}")
        }
    }
}

fun main(args: Array<String>) {
    WidgetTypeInstance()
}