package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.type.Widget

interface GroupChildren {

    var scrollLimit: IntProperty?
    var children: ObjProperty<MutableList<Widget>>?

    fun setScrollLimit(value: Int) {
        scrollLimitProperty().set(value)
    }

    fun getScrollLimit(): Int {
        return scrollLimitProperty().get()
    }

    fun scrollLimitProperty(): IntProperty {
        if (scrollLimit == null)
            scrollLimit = IntProperty(this, "scrollLimit", Settings.getInt(Settings.DEFAULT_CONTAINER_SCROLL_LIMIT))

        return scrollLimit!!
    }

    fun setChildren(value: MutableList<Widget>) {
        childrenProperty().set(value)
    }

    fun getChildren(): MutableList<Widget> {
        return childrenProperty().get()
    }

    fun childrenProperty(): ObjProperty<MutableList<Widget>> {
        if (children == null)
            children = ObjProperty(this, "children", arrayListOf())
        return children!!
    }
}