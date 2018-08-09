package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupChildren {

    var scrollLimit: IntProperty?
    var children: ObjProperty<IntArray>?
    var childX: ObjProperty<IntArray>?
    var childY: ObjProperty<IntArray>?

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

    fun setChildren(value: IntArray) {
        childrenProperty().set(value)
    }

    fun getChildren(): IntArray {
        return childrenProperty().get()
    }

    fun childrenProperty(): ObjProperty<IntArray> {
        if (children == null)
            children = ObjProperty(this, "children", IntArray(0))

        return children!!
    }

    fun setChildX(value: IntArray) {
        childXProperty().set(value)
    }

    fun getChildX(): IntArray {
        return childXProperty().get()
    }

    fun childXProperty(): ObjProperty<IntArray> {
        if (childX == null)
            childX = ObjProperty(this, "childX", IntArray(0))

        return childX!!
    }

    fun setChildY(value: IntArray) {
        childYProperty().set(value)
    }

    fun getChildY(): IntArray {
        return childYProperty().get()
    }

    fun childYProperty(): ObjProperty<IntArray> {
        if (childY == null)
            childY = ObjProperty(this, "childY", IntArray(0))

        return childY!!
    }
}