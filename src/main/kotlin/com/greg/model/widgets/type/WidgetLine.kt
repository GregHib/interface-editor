package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import com.greg.model.widgets.type.groups.GroupLine

class WidgetLine(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupColour, GroupLine {

    var filled = BoolProperty("filled", Settings.getBoolean(Settings.DEFAULT_RECTANGLE_FILLED))
    override var colourProperty = ObjProperty("defaultColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))
    override var lineWidthProperty = IntProperty("defaultColour", 0)
    override var lineMirroredProperty = BoolProperty("lineMirrored", false)


    init {
        properties.add(filled, "Appearance")
        properties.add(colourProperty, "Appearance")
        properties.add(lineWidthProperty, "Layout")
        properties.add(lineMirroredProperty, "Appearance")
    }

    fun setFilled(value: Boolean) { filled.set(value) }

    fun isFilled(): Boolean { return filled.get() }
}