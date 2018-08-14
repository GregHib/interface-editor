package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupSprite {

    var defaultCap: ObjProperty<IntRange>?
    var defaultSprite: IntProperty?
    var defaultSpriteArchive: StringProperty?

    var secondaryCap: ObjProperty<IntRange>?
    var secondarySprite: IntProperty?
    var secondarySpriteArchive: StringProperty?

    fun getDefaultSprite(): Int {
        return defaultSpriteProperty().get()
    }

    fun setDefaultSprite(value: Int, contrain: Boolean = true) {
        defaultSpriteProperty().set(if(contrain) MathUtils.constrain(value, getDefaultCap().start, getDefaultCap().endInclusive) else value)
    }

    fun defaultSpriteProperty(): IntProperty {
        if (defaultSprite == null)
            defaultSprite = IntProperty(this, "defaultSprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))

        return defaultSprite!!
    }

    fun setDefaultCap(range: IntRange) {
        defaultCapProperty().set(range)
    }

    fun getDefaultCap(): IntRange {
        return defaultCapProperty().get()
    }

    fun defaultCapProperty(): ObjProperty<IntRange> {
        if (defaultCap == null)
            defaultCap = ObjProperty(this, "defaultCap", IntRange(0, 1))

        return defaultCap!!
    }

    fun getDefaultSpriteArchive(): String {
        return defaultSpriteArchiveProperty().get()
    }

    fun setDefaultSpriteArchive(value: String) {
        defaultSpriteArchiveProperty().set(value)
    }

    fun defaultSpriteArchiveProperty(): StringProperty {
        if (defaultSpriteArchive == null)
            defaultSpriteArchive = StringProperty(this, "defaultSpriteArchive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))

        return defaultSpriteArchive!!
    }

    fun getSecondarySprite(): Int {
        return secondarySpriteProperty().get()
    }

    fun setSecondarySprite(value: Int, contrain: Boolean = true) {
        secondarySpriteProperty().set(if(contrain) MathUtils.constrain(value, getSecondaryCap().start, getSecondaryCap().endInclusive) else value)
    }

    fun secondarySpriteProperty(): IntProperty {
        if (secondarySprite == null)
            secondarySprite = IntProperty(this, "secondarySprite", 0)

        return secondarySprite!!
    }

    fun setSecondaryCap(range: IntRange) {
        secondaryCapProperty().set(range)
    }

    fun getSecondaryCap(): IntRange {
        return secondaryCapProperty().get()
    }

    fun secondaryCapProperty(): ObjProperty<IntRange> {
        if (secondaryCap == null)
            secondaryCap = ObjProperty(this, "secondaryCap", IntRange(0, 1))

        return secondaryCap!!
    }

    fun getSecondarySpriteArchive(): String {
        return secondarySpriteArchiveProperty().get()
    }

    fun setSecondarySpriteArchive(value: String) {
        secondarySpriteArchiveProperty().set(value)
    }

    fun secondarySpriteArchiveProperty(): StringProperty {
        if (secondarySpriteArchive == null)
            secondarySpriteArchive = StringProperty(this, "secondarySpriteArchive", "")

        return secondarySpriteArchive!!
    }
}