package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour

class WidgetModelList(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupColour {
    override var centred = BoolProperty("centred", Settings.getBoolean(Settings.DEFAULT_TEXT_CENTRED))
    override var fontIndex = IntProperty("fontIndex", 0)
    override var fontBounds = ObjProperty("fontBounds", IntValues(0, 3))
    override var shadow = BoolProperty("shadow", Settings.getBoolean(Settings.DEFAULT_TEXT_SHADOW))
    override var defaultColour = ObjProperty("defaultColour", Settings.getColour(Settings.DEFAULT_RECTANGLE_DEFAULT_COLOUR))

    init {
        properties.add(centred)
        properties.addRanged(fontIndex, fontBounds)
        properties.add(shadow)
        properties.add(defaultColour)
    }

}