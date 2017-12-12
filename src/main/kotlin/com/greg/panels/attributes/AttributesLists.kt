package com.greg.panels.attributes

import com.greg.canvas.widget.AttributeWidget
import com.greg.panels.attributes.parts.pane.AttributePaneType

class AttributesLists(widget: AttributeWidget) {

    private var attributes = mutableListOf<AttributeList>()

    init {
        attributes.add(AttributeList(AttributePaneType.PROPERTIES, widget))
        attributes.add(AttributeList(AttributePaneType.LAYOUT, widget))
    }

    fun addProperty(title: String, name: String, type: AttributeType) {
        get(AttributePaneType.PROPERTIES)?.add(title, name, type)
    }

    fun addLayout(title: String, name: String, type: AttributeType) {
        get(AttributePaneType.LAYOUT)?.add(title, name, type)
    }

    private fun get(type: AttributePaneType): AttributeList? {
        return attributes.firstOrNull { it.type == type }
    }

    fun getAttributes(type: AttributePaneType): List<Attribute>? {
        return get(type)?.getList()
    }
}