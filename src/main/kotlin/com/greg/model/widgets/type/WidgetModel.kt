package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.type.groups.GroupAlignment
import com.greg.model.widgets.type.groups.GroupModel

class WidgetModel(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupModel, GroupAlignment {

    override var defaultMediaType = IntProperty("defaultMediaType", 0)
    override var defaultMedia = IntProperty("defaultMedia", 0)
    override var animationProperty = IntProperty("defaultAnimationId", 0)
    override var spriteScale = IntProperty("spriteScale", 0)
    override var spritePitch = IntProperty("spritePitch", 0)
    override var spriteRoll = IntProperty("spriteRoll", 0)
    override var depthBufferProperty = BoolProperty("depthBuffer", true)
    override var viewportXProperty = IntProperty("viewportX", 0)
    override var viewportYProperty = IntProperty("viewportY", 0)
    override var viewportWidthProperty = IntProperty("viewportWidth", 0)
    override var viewportHeightProperty = IntProperty("viewportHeight", 0)
    override var spriteYawProperty = IntProperty("spriteYaw", 0)
    override var centred = BoolProperty("centred", false)

    init {
        properties.add(defaultMediaType)
        properties.add(defaultMedia)
        properties.add(viewportXProperty)
        properties.add(viewportYProperty)
        properties.add(viewportWidthProperty)
        properties.add(viewportHeightProperty)
        properties.add(spriteYawProperty)
        properties.add(animationProperty)
        properties.add(spriteScale)
        properties.add(spritePitch)
        properties.add(spriteRoll)
    }
}