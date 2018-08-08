package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.StringProperty

interface GroupHover {

    var hover: StringProperty?

    fun setHover(value: String) {
        hoverProperty().set(value)
    }

    fun getHover(): String {
        return hoverProperty().get()
    }

    fun hoverProperty(): StringProperty {
        if (hover == null)
            hover = StringProperty(this, "hover", "")

        return hover!!
    }
}