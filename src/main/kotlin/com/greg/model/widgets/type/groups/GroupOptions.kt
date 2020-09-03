package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupOptions {
    var optionsProperty: ObjProperty<Array<String>>

    var options: Array<String>
        get() = optionsProperty.get()
        set(value) = optionsProperty.set(value)
}