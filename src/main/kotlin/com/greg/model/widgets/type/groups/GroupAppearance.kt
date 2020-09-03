package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupAppearance {

    var fontProperty: IntProperty
    var fontBounds: ObjProperty<IntValues>

    var shaded: BoolProperty
    var monochromeProperty: BoolProperty

    var monochrome: Boolean
        get() = monochromeProperty.get()
        set(value) = monochromeProperty.set(value)

    fun setFont(value: Int) {
        fontProperty.set(value)//MathUtils.constrain(value, getFontBounds().first, getFontBounds().last))
    }

    fun getFont(): Int {
        return fontProperty.get()
    }

    fun getFontBounds(): IntValues {
        return fontBounds.get()
    }

    fun setShaded(value: Boolean) {
        shaded.set(value)
    }

    fun isShaded(): Boolean {
        return shaded.get()
    }
}