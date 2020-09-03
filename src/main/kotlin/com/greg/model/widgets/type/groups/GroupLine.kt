package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupLine {

    var lineWidthProperty: IntProperty

    var lineWidth: Int
        get() = lineWidthProperty.get()
        set(value) = lineWidthProperty.set(value)

    var lineMirroredProperty: BoolProperty

    var lineMirrored: Boolean
        get() = lineMirroredProperty.get()
        set(value) = lineMirroredProperty.set(value)
}