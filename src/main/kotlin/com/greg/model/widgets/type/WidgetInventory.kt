package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupActions
import com.greg.model.widgets.type.groups.GroupInventory
import com.greg.model.widgets.type.groups.GroupPadding
import com.sun.xml.internal.fastinfoset.util.StringArray

class WidgetInventory(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupActions, GroupInventory, GroupPadding {
    override var hasActions: BoolProperty? = null
    override var actions: ObjProperty<StringArray>? = null
    override var swappableItems: BoolProperty? = null
    override var useableItems: BoolProperty? = null
    override var replaceItems: BoolProperty? = null
    override var spritePaddingX: IntProperty? = null
    override var spritePaddingY: IntProperty? = null
    override var spriteX: ObjProperty<IntArray>? = null
    override var spriteY: ObjProperty<IntArray>? = null
    override var sprites: ObjProperty<StringArray>? = null
    override var spritesArchive: ObjProperty<StringArray>? = null
    override var spritesIndex: ObjProperty<IntArray>? = null

    init {
        properties.add(swappableItemsProperty())
        properties.add(hasActionsProperty())
        properties.add(useableItemsProperty())
        properties.add(replaceItemsProperty())
        properties.add(spritePaddingXProperty())
        properties.add(spritePaddingYProperty())
    }

}