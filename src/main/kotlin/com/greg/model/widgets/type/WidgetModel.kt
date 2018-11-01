package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.type.groups.GroupModel

class WidgetModel(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupModel {

    override var defaultMediaType = IntProperty("defaultMediaType", 0)
    override var defaultMedia = IntProperty("defaultMedia", 0)
    override var secondaryMediaType = IntProperty("secondaryMediaType", 0)
    override var secondaryMedia = IntProperty("secondaryMedia", 0)
    override var defaultAnimationId = IntProperty("defaultAnimationId", 0)
    override var secondaryAnimationId = IntProperty("secondaryAnimationId", 0)
    override var spriteScale = IntProperty("spriteScale", 0)
    override var spritePitch = IntProperty("spritePitch", 0)
    override var spriteRoll = IntProperty("spriteRoll", 0)

    init {
        properties.add(defaultMediaType)
        properties.add(defaultMedia)
        properties.add(secondaryMediaType)
        properties.add(secondaryMedia)
        properties.add(defaultAnimationId)
        properties.add(secondaryAnimationId)
        properties.add(spriteScale)
        properties.add(spritePitch)
        properties.add(spriteRoll)
    }

}