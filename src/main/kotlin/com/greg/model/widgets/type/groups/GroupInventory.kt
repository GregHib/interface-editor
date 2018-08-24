package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupInventory {

    var swappableItems: BoolProperty
    var usableItems: BoolProperty
    var replaceItems: BoolProperty
    var spriteX: ObjProperty<IntArray>
    var spriteY: ObjProperty<IntArray>
    var spritesArchive: ObjProperty<Array<String?>>
    var spritesIndex: ObjProperty<Array<Int?>>

    fun setSwappableItems(value: Boolean) { swappableItems.set(value) }

    fun getSwappableItems(): Boolean { return swappableItems.get() }

    fun setUsableItems(value: Boolean) { usableItems.set(value) }

    fun hasUsableItems(): Boolean { return usableItems.get() }

    fun setReplaceItems(value: Boolean) { replaceItems.set(value) }

    fun isReplaceItems(): Boolean { return replaceItems.get() }

    fun setSpriteX(value: IntArray) { spriteX.set(value) }

    fun getSpriteX(): IntArray { return spriteX.get() }

    fun setSpriteY(value: IntArray) { spriteY.set(value) }

    fun getSpriteY(): IntArray { return spriteY.get() }

    fun setSpritesArchive(value: Array<String?>) { spritesArchive.set(value) }

    fun getSpritesArchive(): Array<String?> { return spritesArchive.get() }

    fun setSpritesIndex(value: Array<Int?>) { spritesIndex.set(value) }

    fun getSpritesIndex(): Array<Int?> { return spritesIndex.get() }
}