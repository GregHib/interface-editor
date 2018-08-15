package com.greg.model.widgets.type

import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupChildren
import javafx.collections.ObservableList

class WidgetContainer(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupChildren {
    override var scrollLimit: IntProperty? = null
    override var children: ObjProperty<ObservableList<Widget>>? = null

    init {
        properties.add(scrollLimitProperty())
        properties.addPanel(childrenProperty(), false)
    }

}