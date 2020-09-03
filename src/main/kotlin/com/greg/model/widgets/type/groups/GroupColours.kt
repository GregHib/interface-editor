package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

interface GroupColours {
    var secondaryColour: ObjProperty<Color>

    fun setSecondaryColour(value: Color) { secondaryColour.set(value) }

    fun getSecondaryColour(): Color { return secondaryColour.get() }
}