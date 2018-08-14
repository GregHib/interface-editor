package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty

interface GroupPadding {

    var spritePaddingX: IntProperty?
    var spritePaddingY: IntProperty?

    fun setSpritePaddingX(value: Int) { spritePaddingXProperty().set(value) }

    fun getSpritePaddingX(): Int { return spritePaddingXProperty().get() }

    fun spritePaddingXProperty(): IntProperty {
        if (spritePaddingX == null)
            spritePaddingX = IntProperty(this, "spritePaddingX", 0)

        return spritePaddingX!!
    }

    fun setSpritePaddingY(value: Int) { spritePaddingYProperty().set(value) }

    fun getSpritePaddingY(): Int { return spritePaddingYProperty().get() }

    fun spritePaddingYProperty(): IntProperty {
        if (spritePaddingY == null)
            spritePaddingY = IntProperty(this, "spritePaddingY", 0)

        return spritePaddingY!!
    }
}