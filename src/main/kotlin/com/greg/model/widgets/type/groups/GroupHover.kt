package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.StringProperty

interface GroupHover {

    var hover: StringProperty

    fun setHover(value: String) {
        hover.set(value)
    }

    fun getHover(): String {
        return hover.get()
    }
}