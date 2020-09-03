package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupActions
import com.greg.model.widgets.type.groups.GroupInventory
import com.greg.model.widgets.type.groups.GroupPadding

class WidgetInventory(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupActions, GroupInventory, GroupPadding {
    override var hasActions = BoolProperty("hasActions", Settings.getBoolean(Settings.DEFAULT_HAS_ACTIONS))
    override var actions: ObjProperty<Array<String>> = ObjProperty("actions", Array(0) { "" })
    override var swappableItems = BoolProperty("swappableItems", false)
    override var usableItems = BoolProperty("usableItems", false)
    override var replaceItems = BoolProperty("replaceItems", false)
    override var spritePaddingX = IntProperty("spritePaddingX", 0)
    override var spritePaddingY = IntProperty("spritePaddingY", 0)
    override var spriteX = ObjProperty("spriteX", IntArray(0))
    override var spriteY = ObjProperty("spriteY", IntArray(0))
    override var spritesArchive: ObjProperty<IntArray> = ObjProperty("spritesArchive", IntArray(0))
    override var spritesIndex: ObjProperty<IntArray> = ObjProperty("spritesIndex", IntArray(0))
    override var slotWidth = IntProperty("slotWidth", 0)
    override var slotHeight = IntProperty("slotHeight", 0)
    var arrayRange = ObjProperty("arrayRange", IntValues(0, 0))

    init {
        properties.add(width, "Layout").property.setDisabled(true)
        properties.add(height, "Layout").property.setDisabled(true)
        properties.add(swappableItems)
        properties.add(hasActions)
        properties.add(usableItems)
        properties.add(replaceItems)
        properties.add(spritePaddingX)
        properties.add(spritePaddingY)
        properties.add(slotWidth)
        properties.add(slotHeight)
        properties.addRanged(spriteX, arrayRange)
        properties.addRanged(spriteY, arrayRange)
        properties.addRanged(spritesIndex, arrayRange)
        properties.addRanged(spritesArchive, arrayRange)
    }

}