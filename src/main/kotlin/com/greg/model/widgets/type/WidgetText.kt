package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import com.greg.model.widgets.type.groups.GroupText

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupText, GroupColour {

    override var fontProperty = IntProperty("font", 0)
    override var shaded = BoolProperty("shadow", Settings.getBoolean(Settings.DEFAULT_TEXT_SHADOW))
    override var monochromeProperty = BoolProperty("monochrome", false)

    override var textProperty = StringProperty("defaultText", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))
    override var lineHeightProperty = IntProperty("lineHeight", 0)
    override var horizontalAlignProperty = IntProperty("horizontalAlign", 0)
    override var verticalAlignProperty = IntProperty("verticalAlign", 0)
    override var alignBoundsProperty = ObjProperty("alignBounds", IntValues(0, 2))
    override var lineCountProperty = IntProperty("lineCount", 1)

    override var fontBounds = ObjProperty("fontBounds", IntValues(0, 3))
    override var colourProperty = ObjProperty("defaultColour", Settings.getColour(Settings.DEFAULT_TEXT_DEFAULT_COLOUR))

    init {
        properties.add(fontProperty, "Appearance")
        properties.add(shaded, "Appearance")
        properties.add(monochromeProperty, "Appearance")
        properties.add(colourProperty, "Appearance")

        properties.add(textProperty, "Text")
        properties.add(fontProperty/*, fontBounds*/, "Text")
        properties.add(lineHeightProperty, "Text")
        properties.addRanged(horizontalAlignProperty, alignBoundsProperty, "Text")
        properties.addRanged(verticalAlignProperty, alignBoundsProperty, "Text")
        properties.add(lineCountProperty, "Text")
    }

}