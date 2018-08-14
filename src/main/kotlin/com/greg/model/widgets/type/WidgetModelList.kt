package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import javafx.scene.paint.Color

class WidgetModelList(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupAppearance, GroupColour {
    override var centred: BoolProperty? = null
    override var fontIndex: IntProperty? = null
    override var fontBounds: ObjProperty<IntRange>? = null
    override var shadow: BoolProperty? = null
    override var defaultColour: ObjProperty<Color>? = null

    init {
        properties.add(centredProperty())
        properties.addCapped(fontIndexProperty(), fontBoundsProperty())
        properties.add(shadowProperty())
        properties.add(defaultColourProperty())
    }

}