package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.groups.GroupContainer
import javafx.collections.ObservableList
import tornadofx.observableList

class WidgetContainer(builder: WidgetBuilder, id: Int) : Widget(builder, id), GroupContainer {

    override var scrollWidthProperty = IntProperty("scrollWidth", Settings.getInt(Settings.DEFAULT_CONTAINER_SCROLL_WIDTH))
    override var scrollHeightProperty = IntProperty("scrollHeight", Settings.getInt(Settings.DEFAULT_CONTAINER_SCROLL_WIDTH))

    override var children: ObjProperty<ObservableList<Widget>> = ObjProperty("children", observableList())

    init {
        properties.add(scrollWidthProperty)
        properties.add(scrollHeightProperty)
        properties.addPanel(children, false)
    }

}