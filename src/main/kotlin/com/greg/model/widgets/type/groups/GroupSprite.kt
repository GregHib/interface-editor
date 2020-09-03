package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupSprite {

    var spriteIndexBounds: ObjProperty<IntValues>
    var spriteIndexProperty: IntProperty
    var spriteProperty: IntProperty

    var repeatProperty: BoolProperty

    var repeat: Boolean
        get() = repeatProperty.get()
        set(value) = repeatProperty.set(value)

    var rotationProperty: IntProperty

    var rotation: Int
        get() = rotationProperty.get()
        set(value) = rotationProperty.set(value)

    var orientationProperty: IntProperty

    var orientation: Int
        get() = orientationProperty.get()
        set(value) = orientationProperty.set(value)

    var flipVerticalProperty: BoolProperty

    var flipVertical: Boolean
        get() = flipVerticalProperty.get()
        set(value) = flipVerticalProperty.set(value)

    var flipHorizontalProperty: BoolProperty

    var flipHorizontal: Boolean
        get() = flipHorizontalProperty.get()
        set(value) = flipHorizontalProperty.set(value)

    fun getSpriteIndex(): Int {
        return spriteIndexProperty.get()
    }

    fun setSpriteIndex(value: Int, constrain: Boolean = true) {
        spriteIndexProperty.set(if (constrain) MathUtils.constrain(value, getDefaultCap().first, getDefaultCap().last) else value)
    }

    fun setDefaultCap(range: IntValues) {
        spriteIndexBounds.set(range)
    }

    fun getDefaultCap(): IntValues {
        return spriteIndexBounds.get()
    }

    fun getSprite(): Int {
        return spriteProperty.get()
    }

    fun setSprite(value: Int) {
        spriteProperty.set(value)
    }
}