package com.greg.model.widgets

import com.greg.model.widgets.type.Widget

open class WidgetBuilder(val type: WidgetType) {

    companion object {
        var identifier = 0

        fun getId() : Int {
            return identifier++
        }
    }

    open fun build(id: Int = -1): Widget {
        if(id != -1 && id > identifier)
            identifier = id + 1

        val identifier = if(id != -1) id else getId()

        return type.widget.constructors.first().call(this, identifier)
    }
}