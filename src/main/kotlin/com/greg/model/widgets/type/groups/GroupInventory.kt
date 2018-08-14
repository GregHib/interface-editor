package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.sun.xml.internal.fastinfoset.util.StringArray

interface GroupInventory {

    var swappableItems: BoolProperty?
    var useableItems: BoolProperty?
    var replaceItems: BoolProperty?
    var spriteX: ObjProperty<IntArray>?
    var spriteY: ObjProperty<IntArray>?
    var sprites: ObjProperty<StringArray>?
    var spritesArchive: ObjProperty<StringArray>?
    var spritesIndex: ObjProperty<IntArray>?

    fun setSwappableItems(value: Boolean) { swappableItemsProperty().set(value) }

    fun getSwappableItems(): Boolean { return swappableItemsProperty().get() }

    fun swappableItemsProperty(): BoolProperty {
        if (swappableItems == null)
            swappableItems = BoolProperty(this, "swappableItems", false)

        return swappableItems!!
    }

    fun setUsableItems(value: Boolean) { useableItemsProperty().set(value) }

    fun hasUsableItems(): Boolean { return useableItemsProperty().get() }

    fun useableItemsProperty(): BoolProperty {
        if (useableItems == null)
            useableItems = BoolProperty(this, "useableItems", false)

        return useableItems!!
    }

    fun setReplaceItems(value: Boolean) { replaceItemsProperty().set(value) }

    fun isReplaceItems(): Boolean { return replaceItemsProperty().get() }

    fun replaceItemsProperty(): BoolProperty {
        if (replaceItems == null)
            replaceItems = BoolProperty(this, "replaceItems", false)

        return replaceItems!!
    }

    fun setSpriteX(value: IntArray) { spriteXProperty().set(value) }

    fun getSpriteX(): IntArray { return spriteXProperty().get() }

    fun spriteXProperty(): ObjProperty<IntArray> {
        if (spriteX == null)
            spriteX = ObjProperty(this, "spriteX", IntArray(0))

        return spriteX!!
    }

    fun setSpriteY(value: IntArray) { spriteYProperty().set(value) }

    fun getSpriteY(): IntArray { return spriteYProperty().get() }

    fun spriteYProperty(): ObjProperty<IntArray> {
        if (spriteY == null)
            spriteY = ObjProperty(this, "spriteY", IntArray(0))

        return spriteY!!
    }

    fun setSprites(value: StringArray) { spritesProperty().set(value) }

    fun getSprites(): StringArray { return spritesProperty().get() }

    fun spritesProperty(): ObjProperty<StringArray> {
        if (sprites == null)
            sprites = ObjProperty(this, "sprites", StringArray())

        return sprites!!
    }

    fun setSpritesArchive(value: StringArray) { spritesArchiveProperty().set(value) }

    fun getSpritesArchive(): StringArray { return spritesArchiveProperty().get() }

    fun spritesArchiveProperty(): ObjProperty<StringArray> {
        if (spritesArchive == null)
            spritesArchive = ObjProperty(this, "spritesArchive", StringArray())

        return spritesArchive!!
    }

    fun setSpritesIndex(value: IntArray) { spritesIndexProperty().set(value) }

    fun getSpritesIndex(): IntArray { return spritesIndexProperty().get() }

    fun spritesIndexProperty(): ObjProperty<IntArray> {
        if (spritesIndex == null)
            spritesIndex = ObjProperty(this, "spritesIndex", IntArray(0))

        return spritesIndex!!
    }
}