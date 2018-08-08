package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupAppearance {

    var centred: BoolProperty?
    var fontIndex: IntProperty?
    var shadow: BoolProperty?
    var fontBounds: ObjProperty<IntRange>?

    fun setCentred(value: Boolean) {
        centredProperty().set(value)
    }

    fun isCentred(): Boolean {
        return centredProperty().get()
    }

    fun centredProperty(): BoolProperty {
        if (centred == null)
            centred = BoolProperty(this, "centred", Settings.getBoolean(Settings.DEFAULT_TEXT_CENTRED))

        return centred!!
    }

    fun setFontIndex(value: Int) {
        fontIndexProperty().set(value)
    }

    fun getFontIndex(): Int {
        return fontIndexProperty().get()
    }

    fun fontIndexProperty(): IntProperty {
        if (fontIndex == null)
            fontIndex = IntProperty(this, "fontIndex", 0)

        return fontIndex!!
    }

    fun fontBoundsProperty(): ObjProperty<IntRange> {
        if(fontBounds == null)
            fontBounds = ObjProperty(this, "fontBounds", IntRange(0, 3))

        return fontBounds!!
    }

    fun setShadow(value: Boolean) {
        shadowProperty().set(value)
    }

    fun hasShadow(): Boolean {
        return shadowProperty().get()
    }

    fun shadowProperty(): BoolProperty {
        if (shadow == null)
            shadow = BoolProperty(this, "shadow", Settings.getBoolean(Settings.DEFAULT_TEXT_SHADOW))

        return shadow!!
    }
}