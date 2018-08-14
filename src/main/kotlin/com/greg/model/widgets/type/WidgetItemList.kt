package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupActions
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupPadding
import com.sun.xml.internal.fastinfoset.util.StringArray
import javafx.scene.paint.Color

class WidgetItemList(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupColour, GroupPadding, GroupActions {

    override var centred: BoolProperty? = null
    override var fontIndex: IntProperty? = null
    override var fontBounds: ObjProperty<IntRange>? = null
    override var shadow: BoolProperty? = null
    override var defaultColour: ObjProperty<Color>? = null
    override var spritePaddingX: IntProperty? = null
    override var spritePaddingY: IntProperty? = null
    override var hasActions: BoolProperty? = null
    override var actions: ObjProperty<StringArray>? = null

    init {
        properties.add(centredProperty())
        properties.addCapped(fontIndexProperty(), fontBoundsProperty())
        properties.add(shadowProperty())
        properties.add(defaultColourProperty())
        properties.add(spritePaddingXProperty())
        properties.add(spritePaddingYProperty())
        properties.add(hasActionsProperty())
    }

}