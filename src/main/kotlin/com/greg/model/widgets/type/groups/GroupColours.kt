package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.WidgetText
import javafx.scene.paint.Color

interface GroupColours {
    var secondaryColour: ObjProperty<Color>?
    var defaultHoverColour: ObjProperty<Color>?
    var secondaryHoverColour: ObjProperty<Color>?

    fun setSecondaryColour(value: Color) { secondaryColourProperty().set(value) }

    fun getSecondaryColour(): Color { return secondaryColourProperty().get() }

    fun secondaryColourProperty(): ObjProperty<Color> {
        if (secondaryColour == null)
            secondaryColour = ObjProperty(this, "secondaryColour", Settings.getColour(if(this is WidgetText) Settings.DEFAULT_TEXT_SECONDARY_COLOUR else Settings.DEFAULT_RECTANGLE_SECONDARY_COLOUR))

        return secondaryColour!!
    }

    fun setDefaultHoverColour(value: Color) { defaultHoverColourProperty().set(value) }

    fun getDefaultHoverColour(): Color { return defaultHoverColourProperty().get() }

    fun defaultHoverColourProperty(): ObjProperty<Color> {
        if (defaultHoverColour == null)
            defaultHoverColour = ObjProperty(this, "defaultHoverColour", Settings.getColour(if(this is WidgetText) Settings.DEFAULT_TEXT_DEFAULT_HOVER_COLOUR else Settings.DEFAULT_RECTANGLE_DEFAULT_HOVER_COLOUR))

        return defaultHoverColour!!
    }

    fun setSecondaryHoverColour(value: Color) { secondaryHoverColourProperty().set(value) }

    fun getSecondaryHoverColour(): Color { return secondaryHoverColourProperty().get() }

    fun secondaryHoverColourProperty(): ObjProperty<Color> {
        if (secondaryHoverColour == null)
            secondaryHoverColour = ObjProperty(this, "secondaryHoverColour", Settings.getColour(if(this is WidgetText) Settings.DEFAULT_TEXT_SECONDARY_HOVER_COLOUR else Settings.DEFAULT_RECTANGLE_SECONDARY_HOVER_COLOUR))

        return secondaryHoverColour!!
    }
}