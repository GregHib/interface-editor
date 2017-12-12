package com.greg.panels.attributes

import com.greg.canvas.widget.AttributeWidget
import com.greg.panels.attributes.parts.pane.AttributePaneType

class AttributeList(val type: AttributePaneType, val widget: AttributeWidget) {

    private val attributes = mutableListOf<Attribute>()

    fun add(title: String, name: String, type: AttributeType) {
        val attribute = Attribute(title, name, type)
        attribute.init(widget)
        attributes.add(attribute)
    }

    fun getList(): List<Attribute> {
        return attributes
    }
}