package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.type.groups.GroupModel

class WidgetModel(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupModel {

    override var defaultMediaType: IntProperty? = null
    override var defaultMedia: IntProperty? = null
    override var secondaryMediaType: IntProperty? = null
    override var secondaryMedia: IntProperty? = null
    override var defaultAnimationId: IntProperty? = null
    override var secondaryAnimationId: IntProperty? = null
    override var spriteScale: IntProperty? = null
    override var spritePitch: IntProperty? = null
    override var spriteRoll: IntProperty? = null

    init {
        properties.add(defaultMediaTypeProperty())
        properties.add(defaultMediaProperty())
        properties.add(secondaryMediaTypeProperty())
        properties.add(secondaryMediaProperty())
        properties.add(defaultAnimationIdProperty())
        properties.add(secondaryAnimationIdProperty())
        properties.add(spriteScaleProperty())
        properties.add(spritePitchProperty())
        properties.add(spriteRollProperty())
    }

}