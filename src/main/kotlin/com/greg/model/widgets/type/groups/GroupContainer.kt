package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.Widget
import javafx.collections.ObservableList

interface GroupContainer {

    var scrollWidthProperty: IntProperty

    var scrollWidth: Int
        get() = scrollWidthProperty.get()
        set(value) = scrollWidthProperty.set(value)

    var scrollHeightProperty: IntProperty

    var scrollHeight: Int
        get() = scrollHeightProperty.get()
        set(value) = scrollHeightProperty.set(value)

    var children: ObjProperty<ObservableList<Widget>>

    fun setChildren(value: ObservableList<Widget>) {
        children.set(value)
    }

    fun getChildren(): ObservableList<Widget> {
        return children.get()
    }
}