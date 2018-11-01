package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty

interface GroupModel {

    var defaultMediaType: IntProperty
    var defaultMedia: IntProperty
    var secondaryMediaType: IntProperty
    var secondaryMedia: IntProperty
    var defaultAnimationId: IntProperty
    var secondaryAnimationId: IntProperty
    var spriteScale: IntProperty
    var spritePitch: IntProperty
    var spriteRoll: IntProperty

    fun setDefaultMediaType(value: Int) { defaultMediaType.set(value) }

    fun getDefaultMediaType(): Int { return defaultMediaType.get() }

    fun setDefaultMedia(value: Int) { defaultMedia.set(value) }

    fun getDefaultMedia(): Int { return defaultMedia.get() }

    fun setSecondaryMediaType(value: Int) { secondaryMediaType.set(value) }

    fun getSecondaryMediaType(): Int { return secondaryMediaType.get() }

    fun setSecondaryMedia(value: Int) { secondaryMedia.set(value) }

    fun getSecondaryMedia(): Int { return secondaryMedia.get() }

    fun setDefaultAnimationId(value: Int) { defaultAnimationId.set(value) }

    fun getDefaultAnimationId(): Int { return defaultAnimationId.get() }

    fun setSecondaryAnimationId(value: Int) { secondaryAnimationId.set(value) }

    fun getSecondaryAnimationId(): Int { return secondaryAnimationId.get() }

    fun setSpriteScale(value: Int) { spriteScale.set(value) }

    fun getSpriteScale(): Int { return spriteScale.get() }

    fun setSpritePitch(value: Int) { spritePitch.set(value) }

    fun getSpritePitch(): Int { return spritePitch.get() }

    fun setSpriteRoll(value: Int) { spriteRoll.set(value) }

    fun getSpriteRoll(): Int { return spriteRoll.get() }
}