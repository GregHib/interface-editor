package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupActions
import com.greg.model.widgets.type.groups.GroupInventory
import com.greg.model.widgets.type.groups.GroupPadding

class WidgetInventory(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupActions, GroupInventory, GroupPadding {
    override var hasActions = BoolProperty(this, "hasActions", Settings.getBoolean(Settings.DEFAULT_HAS_ACTIONS))
    override var actions: ObjProperty<Array<String?>> = ObjProperty(this, "actions", arrayOfNulls(0))
    override var swappableItems: BoolProperty? = null
    override var usableItems: BoolProperty? = null
    override var replaceItems: BoolProperty? = null
    override var spritePaddingX: IntProperty? = null
    override var spritePaddingY: IntProperty? = null
    override var spriteX: ObjProperty<IntArray>? = null
    override var spriteY: ObjProperty<IntArray>? = null
    override var spritesArchive: ObjProperty<Array<String?>>? = null
    override var spritesIndex: ObjProperty<Array<Int?>>? = null

    init {
        properties.add(swappableItemsProperty())
        properties.add(hasActions)
        properties.add(usableItemsProperty())
        properties.add(replaceItemsProperty())
        properties.add(spritePaddingXProperty())
        properties.add(spritePaddingYProperty())
    }

}