package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupSprite

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupSprite {
    override var defaultCap: ObjProperty<IntRange>? = null
    override var defaultSprite: IntProperty? = null
    override var defaultSpriteArchive: StringProperty? = null
    override var secondaryCap: ObjProperty<IntRange>? = null
    override var secondarySprite: IntProperty? = null
    override var secondarySpriteArchive: StringProperty? = null

    init {
        properties.add(widthProperty(), "Layout").property.setDisabled(true)
        properties.add(heightProperty(), "Layout").property.setDisabled(true)
        properties.addCapped(defaultSpriteProperty(), defaultCapProperty())
        properties.add(defaultSpriteArchiveProperty())
        properties.addCapped(secondarySpriteProperty(), secondaryCapProperty())
        properties.add(secondarySpriteArchiveProperty())
    }
}