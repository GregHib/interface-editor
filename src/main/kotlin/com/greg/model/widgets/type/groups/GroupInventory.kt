package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupInventory {

    var swappableItems: BoolProperty?
    var usableItems: BoolProperty?
    var replaceItems: BoolProperty?
    var spriteX: ObjProperty<IntArray>?
    var spriteY: ObjProperty<IntArray>?
    var spritesArchive: ObjProperty<Array<String?>>?
    var spritesIndex: ObjProperty<Array<Int?>>?

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
        if (usableItems == null)
            usableItems = BoolProperty(this, "usableItems", false)

        return usableItems!!
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

    fun setSpritesArchive(value: Array<String?>) { spritesArchiveProperty().set(value) }

    fun getSpritesArchive(): Array<String?> { return spritesArchiveProperty().get() }

    fun spritesArchiveProperty(): ObjProperty<Array<String?>> {
        if (spritesArchive == null)
            spritesArchive = ObjProperty(this, "spritesArchive", arrayOfNulls(0))

        return spritesArchive!!
    }

    fun setSpritesIndex(value: Array<Int?>) { spritesIndexProperty().set(value) }

    fun getSpritesIndex(): Array<Int?> { return spritesIndexProperty().get() }

    fun spritesIndexProperty(): ObjProperty<Array<Int?>> {
        if (spritesIndex == null)
            spritesIndex = ObjProperty(this, "spritesIndex", arrayOfNulls(0))

        return spritesIndex!!
    }
}