package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupColour, GroupColours {

    var filled = BoolProperty("filled", Settings.getBoolean(Settings.DEFAULT_RECTANGLE_FILLED))
    override var defaultColour = ObjProperty("defaultColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))
    override var secondaryColour = ObjProperty("secondaryColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_SECONDARY_COLOUR))
    override var defaultHoverColour = ObjProperty("defaultHoverColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_HOVER_COLOUR))
    override var secondaryHoverColour = ObjProperty("secondaryHoverColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_SECONDARY_HOVER_COLOUR))

    init {
        properties.add(filled)
        properties.add(defaultColour)
        properties.add(secondaryColour)
        properties.add(defaultHoverColour)
        properties.add(secondaryHoverColour)
    }

    fun setFilled(value: Boolean) { filled.set(value) }

    fun isFilled(): Boolean { return filled.get() }
}