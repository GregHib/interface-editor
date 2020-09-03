package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty

interface GroupHover {
    var disableHoverProperty: BoolProperty

    var hover: Boolean
        get() = disableHoverProperty.get()
        set(value) = disableHoverProperty.set(value)
}