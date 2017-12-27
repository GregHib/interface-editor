package com.greg.ui.canvas.widget.builder.data

import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.Attribute
import com.greg.ui.panel.panels.attribute.AttributeType

/*
    A list of lists
    Attributes is a list of AttributeList
 */
class Attributes(facade: WidgetFacade) {

    private var attributes = mutableListOf<AttributeList>()

    init {
        attributes.add(AttributeList(PanelType.PROPERTIES, facade))
        attributes.add(AttributeList(PanelType.LAYOUT, facade))
    }

    fun addProperty(title: String, name: String, type: AttributeType) {
        get(PanelType.PROPERTIES)?.add(title, name, type)
    }

    fun addLayout(title: String, name: String, type: AttributeType) {
        get(PanelType.LAYOUT)?.add(title, name, type)
    }

    private fun get(type: PanelType): AttributeList? {
        return attributes.firstOrNull { it.type == type }
    }

    fun getAttributes(type: PanelType): List<Attribute>? {
        return get(type)?.getList()
    }
}