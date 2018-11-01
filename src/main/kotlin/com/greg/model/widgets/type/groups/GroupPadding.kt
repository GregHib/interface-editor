package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty

interface GroupPadding {

    var spritePaddingX: IntProperty
    var spritePaddingY: IntProperty

    fun setSpritePaddingX(value: Int) { spritePaddingX.set(value) }

    fun getSpritePaddingX(): Int { return spritePaddingX.get() }

    fun setSpritePaddingY(value: Int) { spritePaddingY.set(value) }

    fun getSpritePaddingY(): Int { return spritePaddingY.get() }
}