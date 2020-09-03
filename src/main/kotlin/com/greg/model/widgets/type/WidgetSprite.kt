package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupSprite

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupSprite {
    override var defaultCap = ObjProperty("defaultCap", IntValues(0, 1))
    override var defaultSprite = IntProperty("defaultSprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))
    override var defaultSpriteArchive = IntProperty("defaultSpriteArchive", Settings.getInt(Settings.DEFAULT_SPRITE_ARCHIVE))
    override var secondaryCap = ObjProperty("secondaryCap", IntValues(0, 1))
    override var secondarySprite = IntProperty("secondarySprite", 0)
    override var secondarySpriteArchive = IntProperty("secondarySpriteArchive", 0)
    override var repeatsImage = BoolProperty( "repeats", false)

    init {
        properties.add(width, "Layout")
        properties.add(height, "Layout")
        heightBounds.set(IntValues.EMPTY)
        widthBounds.set(IntValues.EMPTY)
        properties.add(defaultSpriteArchive)// TODO caps
        properties.addRanged(defaultSprite, defaultCap)
        properties.add(secondarySpriteArchive)
        properties.addRanged(secondarySprite, secondaryCap)
    }
}