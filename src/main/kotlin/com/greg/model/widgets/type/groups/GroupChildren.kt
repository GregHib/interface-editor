package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.Widget
import javafx.collections.ObservableList

interface GroupChildren {

    var scrollLimit: IntProperty
    var children: ObjProperty<ObservableList<Widget>>

    fun setScrollLimit(value: Int) {
        scrollLimit.set(value)
    }

    fun getScrollLimit(): Int {
        return scrollLimit.get()
    }

    fun setChildren(value: ObservableList<Widget>) {
        children.set(value)
    }

    fun getChildren(): ObservableList<Widget> {
        return children.get()
    }
}