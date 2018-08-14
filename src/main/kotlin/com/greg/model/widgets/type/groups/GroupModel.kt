package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty

interface GroupModel {

    var defaultMediaType: IntProperty?
    var defaultMedia: IntProperty?
    var secondaryMediaType: IntProperty?
    var secondaryMedia: IntProperty?
    var defaultAnimationId: IntProperty?
    var secondaryAnimationId: IntProperty?
    var spriteScale: IntProperty?
    var spritePitch: IntProperty?
    var spriteRoll: IntProperty?

    fun setDefaultMediaType(value: Int) { defaultMediaTypeProperty().set(value) }

    fun getDefaultMediaType(): Int { return defaultMediaTypeProperty().get() }

    fun defaultMediaTypeProperty(): IntProperty {
        if (defaultMediaType == null)
            defaultMediaType = IntProperty(this, "defaultMediaType", 0)

        return defaultMediaType!!
    }

    fun setDefaultMedia(value: Int) { defaultMediaProperty().set(value) }

    fun getDefaultMedia(): Int { return defaultMediaProperty().get() }

    fun defaultMediaProperty(): IntProperty {
        if (defaultMedia == null)
            defaultMedia = IntProperty(this, "defaultMedia", 0)

        return defaultMedia!!
    }

    fun setSecondaryMediaType(value: Int) { secondaryMediaTypeProperty().set(value) }

    fun getSecondaryMediaType(): Int { return secondaryMediaTypeProperty().get() }

    fun secondaryMediaTypeProperty(): IntProperty {
        if (secondaryMediaType == null)
            secondaryMediaType = IntProperty(this, "secondaryMediaType", 0)

        return secondaryMediaType!!
    }

    fun setSecondaryMedia(value: Int) { secondaryMediaProperty().set(value) }

    fun getSecondaryMedia(): Int { return secondaryMediaProperty().get() }

    fun secondaryMediaProperty(): IntProperty {
        if (secondaryMedia == null)
            secondaryMedia = IntProperty(this, "secondaryMedia", 0)

        return secondaryMedia!!
    }

    fun setDefaultAnimationId(value: Int) { defaultAnimationIdProperty().set(value) }

    fun getDefaultAnimationId(): Int { return defaultAnimationIdProperty().get() }

    fun defaultAnimationIdProperty(): IntProperty {
        if (defaultAnimationId == null)
            defaultAnimationId = IntProperty(this, "defaultAnimationId", 0)

        return defaultAnimationId!!
    }

    fun setSecondaryAnimationId(value: Int) { secondaryAnimationIdProperty().set(value) }

    fun getSecondaryAnimationId(): Int { return secondaryAnimationIdProperty().get() }

    fun secondaryAnimationIdProperty(): IntProperty {
        if (secondaryAnimationId == null)
            secondaryAnimationId = IntProperty(this, "secondaryAnimationId", 0)

        return secondaryAnimationId!!
    }

    fun setSpriteScale(value: Int) { spriteScaleProperty().set(value) }

    fun getSpriteScale(): Int { return spriteScaleProperty().get() }

    fun spriteScaleProperty(): IntProperty {
        if (spriteScale == null)
            spriteScale = IntProperty(this, "spriteScale", 0)

        return spriteScale!!
    }

    fun setSpritePitch(value: Int) { spritePitchProperty().set(value) }

    fun getSpritePitch(): Int { return spritePitchProperty().get() }

    fun spritePitchProperty(): IntProperty {
        if (spritePitch == null)
            spritePitch = IntProperty(this, "spritePitch", 0)

        return spritePitch!!
    }

    fun setSpriteRoll(value: Int) { spriteRollProperty().set(value) }

    fun getSpriteRoll(): Int { return spriteRollProperty().get() }

    fun spriteRollProperty(): IntProperty {
        if (spriteRoll == null)
            spriteRoll = IntProperty(this, "spriteRoll", 0)

        return spriteRoll!!
    }
}