package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import com.greg.model.widgets.type.groups.GroupSprite

class WidgetSprite(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupSprite, GroupColour, GroupColours {

    override var spriteIndexBounds = ObjProperty("defaultCap", IntValues(0, 1))
    override var spriteIndexProperty = IntProperty("spriteIndex", Settings.getInt(Settings.DEFAULT_SPRITE_ID))
    override var spriteProperty = IntProperty("sprite", Settings.getInt(Settings.DEFAULT_SPRITE_ARCHIVE))
    override var repeatProperty = BoolProperty("repeat", false)
    override var rotationProperty = IntProperty("rotation", 0)
    override var orientationProperty = IntProperty("orientation", 0)
    override var flipVerticalProperty = BoolProperty("flipVertical", false)
    override var flipHorizontalProperty = BoolProperty("flipVertical", false)
    override var colourProperty = ObjProperty("colour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))
    override var secondaryColour = ObjProperty("secondaryColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_SECONDARY_COLOUR))

    init {
        properties.add(width, "Layout")
        properties.add(height, "Layout")
        heightBounds.set(IntValues.EMPTY)
        widthBounds.set(IntValues.EMPTY)
        properties.add(spriteProperty, "Sprite")
        properties.addRanged(spriteIndexProperty, spriteIndexBounds, "Sprite")
        properties.add(flipVerticalProperty, "Layout")
        properties.add(flipHorizontalProperty, "Layout")
    }
}