package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty

interface GroupModel {

    var defaultMediaType: IntProperty
    var defaultMedia: IntProperty

    var animationProperty: IntProperty
    var spriteScale: IntProperty
    var spritePitch: IntProperty
    var spriteRoll: IntProperty

    var depthBufferProperty: BoolProperty

    var depthBuffer: Boolean
        get() = depthBufferProperty.get()
        set(value) = depthBufferProperty.set(value)

    var viewportXProperty: IntProperty

    var viewportX: Int
        get() = viewportXProperty.get()
        set(value) = viewportXProperty.set(value)

    var viewportYProperty: IntProperty

    var viewportY: Int
        get() = viewportYProperty.get()
        set(value) = viewportYProperty.set(value)

    var spriteYawProperty: IntProperty

    var spriteYaw: Int
        get() = spriteYawProperty.get()
        set(value) = spriteYawProperty.set(value)

    var viewportWidthProperty: IntProperty

    var viewportWidth: Int
        get() = viewportWidthProperty.get()
        set(value) = viewportWidthProperty.set(value)

    var viewportHeightProperty: IntProperty

    var viewportHeight: Int
        get() = viewportHeightProperty.get()
        set(value) = viewportHeightProperty.set(value)

    fun setDefaultMediaType(value: Int) { defaultMediaType.set(value) }

    fun getDefaultMediaType(): Int { return defaultMediaType.get() }

    fun setDefaultMedia(value: Int) { defaultMedia.set(value) }

    fun getDefaultMedia(): Int { return defaultMedia.get() }

    fun setAnimation(value: Int) { animationProperty.set(value) }

    fun getAnimation(): Int { return animationProperty.get() }

    fun setSpriteScale(value: Int) { spriteScale.set(value) }

    fun getSpriteScale(): Int { return spriteScale.get() }

    fun setSpritePitch(value: Int) { spritePitch.set(value) }

    fun getSpritePitch(): Int { return spritePitch.get() }

    fun setSpriteRoll(value: Int) { spriteRoll.set(value) }

    fun getSpriteRoll(): Int { return spriteRoll.get() }
}