package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupChildren
import javafx.collections.ObservableList
import tornadofx.observableList

class WidgetContainer(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupChildren {
    override var scrollLimit = IntProperty("scrollLimit", Settings.getInt(Settings.DEFAULT_CONTAINER_SCROLL_LIMIT))
    override var children: ObjProperty<ObservableList<Widget>> = ObjProperty("children", observableList())

    init {
        properties.add(scrollLimit)
        properties.addPanel(children, false)
    }

}