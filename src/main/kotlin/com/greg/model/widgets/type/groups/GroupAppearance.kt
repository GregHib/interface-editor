package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupAppearance {

    var centred: BoolProperty
    var fontIndex: IntProperty
    var shadow: BoolProperty
    var fontBounds: ObjProperty<IntRange>

    fun setCentred(value: Boolean) {
        centred.set(value)
    }

    fun isCentred(): Boolean {
        return centred.get()
    }

    fun setFontIndex(value: Int) {
        fontIndex.set(MathUtils.constrain(value, getFontBounds().start, getFontBounds().endInclusive))
    }

    fun getFontIndex(): Int {
        return fontIndex.get()
    }

    fun getFontBounds(): IntRange {
        return fontBounds.get()
    }

    fun setShadow(value: Boolean) {
        shadow.set(value)
    }

    fun hasShadow(): Boolean {
        return shadow.get()
    }
}