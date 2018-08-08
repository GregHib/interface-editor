package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.WidgetText
import javafx.scene.paint.Color

interface GroupColour {

    var defaultColour: ObjProperty<Color>?

    fun setDefaultColour(value: Color) { defaultColourProperty().set(value) }

    fun getDefaultColour(): Color { return defaultColourProperty().get() }

    fun defaultColourProperty(): ObjProperty<Color> {
        if (defaultColour == null)
            defaultColour = ObjProperty(this, "defaultColour", Settings.getColour(if(this is WidgetText) Settings.DEFAULT_TEXT_DEFAULT_COLOUR else Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))

        return defaultColour!!
    }
}