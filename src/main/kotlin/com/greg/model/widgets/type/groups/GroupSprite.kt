package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupSprite {

    var defaultCap: ObjProperty<IntRange>
    var defaultSprite: IntProperty
    var defaultSpriteArchive: StringProperty

    var secondaryCap: ObjProperty<IntRange>
    var secondarySprite: IntProperty
    var secondarySpriteArchive: StringProperty

    fun getDefaultSprite(): Int {
        return defaultSprite.get()
    }

    fun setDefaultSprite(value: Int, constrain: Boolean = true) {
        defaultSprite.set(if(constrain) MathUtils.constrain(value, getDefaultCap().start, getDefaultCap().endInclusive) else value)
    }

    fun setDefaultCap(range: IntRange) {
        defaultCap.set(range)
    }

    fun getDefaultCap(): IntRange {
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
        secondarySprite.set(if(constrain) MathUtils.constrain(value, getSecondaryCap().start, getSecondaryCap().endInclusive) else value)
    }

    fun setSecondaryCap(range: IntRange) {
        secondaryCap.set(range)
    }

    fun getSecondaryCap(): IntRange {
        return secondaryCap.get()
    }

    fun getSecondarySpriteArchive(): String {
        return secondarySpriteArchive.get()
    }

    fun setSecondarySpriteArchive(value: String) {
        secondarySpriteArchive.set(value)
    }
}