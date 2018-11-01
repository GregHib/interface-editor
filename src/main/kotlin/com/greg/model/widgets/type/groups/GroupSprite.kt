package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupSprite {

    var defaultCap: ObjProperty<IntValues>
    var defaultSprite: IntProperty
    var defaultSpriteArchive: StringProperty

    var secondaryCap: ObjProperty<IntValues>
    var secondarySprite: IntProperty
    var secondarySpriteArchive: StringProperty

    fun getDefaultSprite(): Int {
        return defaultSprite.get()
    }

    fun setDefaultSprite(value: Int, constrain: Boolean = true) {
        defaultSprite.set(if(constrain) MathUtils.constrain(value, getDefaultCap().first, getDefaultCap().last) else value)
    }

    fun setDefaultCap(range: IntValues) {
        defaultCap.set(range)
    }

    fun getDefaultCap(): IntValues {
        return defaultCap.get()
    }

    fun getDefaultSpriteArchive(): String {
        return defaultSpriteArchive.get()
    }

    fun setDefaultSpriteArchive(value: String) {
        defaultSpriteArchive.set(value)
    }

    fun getSecondarySprite(): Int {
        return secondarySprite.get()
    }

    fun setSecondarySprite(value: Int, constrain: Boolean = true) {
        secondarySprite.set(if(constrain) MathUtils.constrain(value, getSecondaryCap().first, getSecondaryCap().last) else value)
    }

    fun setSecondaryCap(range: IntValues) {
        secondaryCap.set(range)
    }

    fun getSecondaryCap(): IntValues {
        return secondaryCap.get()
    }

    fun getSecondarySpriteArchive(): String {
        return secondarySpriteArchive.get()
    }

    fun setSecondarySpriteArchive(value: String) {
        secondarySpriteArchive.set(value)
    }
}