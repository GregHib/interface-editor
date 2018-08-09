package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupChildren

class WidgetContainer(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupChildren {
    override var scrollLimit: IntProperty? = null
    override var children: ObjProperty<IntArray>? = null
    override var childX: ObjProperty<IntArray>? = null
    override var childY: ObjProperty<IntArray>? = null

    init {
        properties.add(scrollLimitProperty())
        properties.addPanel(childrenProperty(), false)
        properties.addPanel(childXProperty(), false)
        properties.addPanel(childYProperty(), false)
    }

}