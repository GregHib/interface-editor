package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

interface GroupColours {
    var secondaryColour: ObjProperty<Color>
    var defaultHoverColour: ObjProperty<Color>
    var secondaryHoverColour: ObjProperty<Color>

    fun setSecondaryColour(value: Color) { secondaryColour.set(value) }

    fun getSecondaryColour(): Color { return secondaryColour.get() }

    fun setDefaultHoverColour(value: Color) { defaultHoverColour.set(value) }

    fun getDefaultHoverColour(): Color { return defaultHoverColour.get() }

    fun setSecondaryHoverColour(value: Color) { secondaryHoverColour.set(value) }

    fun getSecondaryHoverColour(): Color { return secondaryHoverColour.get() }
}