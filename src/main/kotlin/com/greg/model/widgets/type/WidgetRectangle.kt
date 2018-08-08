package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupColour, GroupColours {

    private var filled: BoolProperty? = null
    override var defaultColour: ObjProperty<Color>? = null
    override var secondaryColour: ObjProperty<Color>? = null
    override var defaultHoverColour: ObjProperty<Color>? = null
    override var secondaryHoverColour: ObjProperty<Color>? = null

    init {
        properties.add(filledProperty())
        properties.add(defaultColourProperty())
        properties.add(secondaryColourProperty())
        properties.add(defaultHoverColourProperty())
        properties.add(secondaryHoverColourProperty())
    }

    fun setFilled(value: Boolean) { filledProperty().set(value) }

    fun isFilled(): Boolean { return filledProperty().get() }

    fun filledProperty(): BoolProperty {
        if (filled == null)
            filled = BoolProperty(this, "filled", Settings.getBoolean(Settings.DEFAULT_RECTANGLE_FILLED))

        return filled!!
    }
}