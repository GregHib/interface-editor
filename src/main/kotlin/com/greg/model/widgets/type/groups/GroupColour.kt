package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

interface GroupColour {

    var colourProperty: ObjProperty<Color>

    fun setColour(value: Color) { colourProperty.set(value) }

    fun getColour(): Color { return colourProperty.get() }
}