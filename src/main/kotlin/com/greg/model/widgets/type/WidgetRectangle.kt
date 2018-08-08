package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var filled: BoolProperty? = null
    private var defaultColour: ObjProperty<Color>? = null
    private var secondaryColour: ObjProperty<Color>? = null
    private var defaultHoverColour: ObjProperty<Color>? = null
    private var secondaryHoverColour: ObjProperty<Color>? = null

    init {
        properties.add(filledProperty())
        properties.add(defaultColourProperty())
        properties.add(secondaryColourProperty())
        properties.add(defaultHoverColourProperty())
        properties.add(secondaryHoverColourProperty())
    }

    fun setDefaultColour(value: Color) { defaultColourProperty().set(value) }

    fun getDefaultColour(): Color { return defaultColourProperty().get() }

    fun defaultColourProperty(): ObjProperty<Color> {
        if (defaultColour == null)
            defaultColour = ObjProperty(this, "defaultColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))

        return defaultColour!!
    }

    fun setSecondaryColour(value: Color) { secondaryColourProperty().set(value) }

    fun getSecondaryColour(): Color { return secondaryColourProperty().get() }

    fun secondaryColourProperty(): ObjProperty<Color> {
        if (secondaryColour == null)
            secondaryColour = ObjProperty(this, "secondaryColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_SECONDARY_COLOUR))

        return secondaryColour!!
    }

    fun setDefaultHoverColour(value: Color) { defaultHoverColourProperty().set(value) }

    fun getDefaultHoverColour(): Color { return defaultHoverColourProperty().get() }

    fun defaultHoverColourProperty(): ObjProperty<Color> {
        if (defaultHoverColour == null)
            defaultHoverColour = ObjProperty(this, "defaultHoverColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_HOVER_COLOUR))

        return defaultHoverColour!!
    }

    fun setSecondaryHoverColour(value: Color) { secondaryHoverColourProperty().set(value) }

    fun getSecondaryHoverColour(): Color { return secondaryHoverColourProperty().get() }

    fun secondaryHoverColourProperty(): ObjProperty<Color> {
        if (secondaryHoverColour == null)
            secondaryHoverColour = ObjProperty(this, "secondaryHoverColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_SECONDARY_HOVER_COLOUR))

        return secondaryHoverColour!!
    }

    fun setFilled(value: Boolean) { filledProperty().set(value) }

    fun isFilled(): Boolean { return filledProperty().get() }

    fun filledProperty(): BoolProperty {
        if (filled == null)
            filled = BoolProperty(this, "filled", Settings.getBoolean(Settings.DEFAULT_RECTANGLE_FILLED))

        return filled!!
    }
}