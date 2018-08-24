package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupSprite

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupSprite {
    override var defaultCap = ObjProperty(this, "defaultCap", IntRange(0, 1))
    override var defaultSprite = IntProperty(this, "defaultSprite", Settings.getInt(Settings.DEFAULT_SPRITE_ID))
    override var defaultSpriteArchive: StringProperty = StringProperty(this, "defaultSpriteArchive", Settings.get(Settings.DEFAULT_SPRITE_ARCHIVE_NAME))
    override var secondaryCap = ObjProperty(this, "secondaryCap", IntRange(0, 1))
    override var secondarySprite = IntProperty(this, "secondarySprite", 0)
    override var secondarySpriteArchive = StringProperty(this, "secondarySpriteArchive", "")

    init {
        properties.add(widthProperty(), "Layout").property.setDisabled(true)
        properties.add(heightProperty(), "Layout").property.setDisabled(true)
        properties.addCapped(defaultSprite, defaultCap)
        properties.add(defaultSpriteArchive)
        properties.addCapped(secondarySprite, secondaryCap)
        properties.add(secondarySpriteArchive)
    }
}