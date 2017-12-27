package com.greg.ui.canvas.widget.builder.data

import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.Attribute
import com.greg.ui.panel.panels.attribute.AttributeType

class AttributeList(val type: PanelType, private val facade: WidgetFacade) {

    private val attributes = mutableListOf<Attribute>()

    fun add(title: String, name: String, type: AttributeType) {
        val attribute = Attribute(title, name, type)
        attribute.init(facade)
        attributes.add(attribute)
    }

    fun getList(): List<Attribute> {
        return attributes
    }
}