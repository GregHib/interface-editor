package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupSprite

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupSprite {
    override var defaultCap = ObjProperty("defaultCap", IntRange(0, 1))
    override var defaultSprite = IntProperty("defaultSprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))
    override var defaultSpriteArchive: StringProperty = StringProperty("defaultSpriteArchive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))
    override var secondaryCap = ObjProperty("secondaryCap", IntRange(0, 1))
    override var secondarySprite = IntProperty("secondarySprite", 0)
    override var secondarySpriteArchive = StringProperty("secondarySpriteArchive", "")

    init {
        properties.add(width, "Layout").property.setDisabled(true)
        properties.add(height, "Layout").property.setDisabled(true)
        properties.addCapped(defaultSprite, defaultCap)
        properties.add(defaultSpriteArchive)
        properties.addCapped(secondarySprite, secondaryCap)
        properties.add(secondarySpriteArchive)
    }
}