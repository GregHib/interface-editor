package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import com.greg.model.widgets.type.groups.GroupText
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupText, GroupColour, GroupColours {

    override var centred: BoolProperty? = null
    override var fontIndex: IntProperty? = null
    override var fontBounds: ObjProperty<IntRange>? = null
    override var shadow: BoolProperty? = null
    override var defaultText: StringProperty? = null
    override var secondaryText: StringProperty? = null
    override var defaultColour: ObjProperty<Color>? = null
    override var secondaryColour: ObjProperty<Color>? = null
    override var defaultHoverColour: ObjProperty<Color>? = null
    override var secondaryHoverColour: ObjProperty<Color>? = null

    init {
        properties.add(centredProperty())
        properties.addCapped(fontIndexProperty(), fontBoundsProperty())
        properties.add(shadowProperty())
        properties.add(defaultTextProperty())
        properties.add(secondaryTextProperty())
        properties.add(defaultColourProperty())
        properties.add(secondaryColourProperty())
        properties.add(defaultHoverColourProperty())
        properties.add(secondaryHoverColourProperty())
    }

}