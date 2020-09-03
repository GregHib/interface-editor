package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupText {

    var textProperty: StringProperty

    var lineHeightProperty: IntProperty

    var lineHeight: Int
        get() = lineHeightProperty.get()
        set(value) = lineHeightProperty.set(value)

    var horizontalAlignProperty: IntProperty

    var horizontalAlign: Int
        get() = horizontalAlignProperty.get()
        set(value) = horizontalAlignProperty.set(value)

    var verticalAlignProperty: IntProperty

    var verticalAlign: Int
        get() = verticalAlignProperty.get()
        set(value) = verticalAlignProperty.set(value)

    var alignBoundsProperty: ObjProperty<IntValues>

    var alignBounds: IntValues
        get() = alignBoundsProperty.get()
        set(value) = alignBoundsProperty.set(value)

    var lineCountProperty: IntProperty

    var lineCount: Int
        get() = lineCountProperty.get()
        set(value) = lineCountProperty.set(value)

    fun setText(value: String) {
        textProperty.set(value)
    }

    fun getText(): String {
        return textProperty.get()
    }
}