package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import com.greg.model.widgets.type.groups.GroupText

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupText, GroupColour, GroupColours {

    override var centred = BoolProperty("centred", Settings.getBoolean(Settings.DEFAULT_TEXT_CENTRED))
    override var fontIndex = IntProperty("fontIndex", 0)
    override var fontBounds = ObjProperty("fontBounds", IntRange(0, 3))
    override var shadow = BoolProperty("shadow", Settings.getBoolean(Settings.DEFAULT_TEXT_SHADOW))
    override var defaultText = StringProperty("defaultText", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))
    override var secondaryText = StringProperty("secondaryText", Settings.get(Settings.DEFAULT_TEXT_SECONDARY_MESSAGE))
    override var defaultColour = ObjProperty("defaultColour", Settings.getColour(Settings.DEFAULT_TEXT_DEFAULT_COLOUR))
    override var secondaryColour = ObjProperty("secondaryColour", Settings.getColour(Settings.DEFAULT_TEXT_SECONDARY_COLOUR))
    override var defaultHoverColour = ObjProperty("defaultHoverColour", Settings.getColour(Settings.DEFAULT_TEXT_DEFAULT_HOVER_COLOUR))
    override var secondaryHoverColour = ObjProperty("secondaryHoverColour", Settings.getColour(Settings.DEFAULT_TEXT_SECONDARY_HOVER_COLOUR))

    init {
        properties.add(centred)
        properties.addCapped(fontIndex, fontBounds)
        properties.add(shadow)
        properties.add(defaultText)
        properties.add(secondaryText)
        properties.add(defaultColour)
        properties.add(secondaryColour)
        properties.add(defaultHoverColour)
        properties.add(secondaryHoverColour)
    }

}