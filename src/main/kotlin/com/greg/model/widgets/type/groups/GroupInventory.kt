package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupInventory {

    var swappableItems: BoolProperty
    var usableItems: BoolProperty
    var replaceItems: BoolProperty
    var spriteX: ObjProperty<IntArray>
    var spriteY: ObjProperty<IntArray>
    var spritesArchive: ObjProperty<IntArray>
    var spritesIndex: ObjProperty<IntArray>
    var slotWidth: IntProperty
    var slotHeight: IntProperty

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

    fun setSpritesArchive(value: IntArray) { spritesArchive.set(value) }

    fun getSpritesArchive(): IntArray { return spritesArchive.get() }

    fun setSpritesIndex(value: IntArray) { spritesIndex.set(value) }

    fun getSpritesIndex(): IntArray { return spritesIndex.get() }

    fun setSlotWidth(value: Int) { slotWidth.set(value) }

    fun getSlotWidth(): Int { return slotWidth.get() }

    fun setSlotHeight(value: Int) { slotHeight.set(value) }

    fun getSlotHeight(): Int { return slotHeight.get() }
}