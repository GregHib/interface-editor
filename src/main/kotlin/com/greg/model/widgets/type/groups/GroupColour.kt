package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

interface GroupColour {

    var defaultColour: ObjProperty<Color>

    fun setDefaultColour(value: Color) { defaultColour.set(value) }

    fun getDefaultColour(): Color { return defaultColour.get() }
}