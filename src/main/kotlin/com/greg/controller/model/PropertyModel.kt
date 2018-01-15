package com.greg.controller.model

import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.panel.panels.PanelType
import javafx.beans.property.Property

class PropertyModel {

    private val properties = mutableListOf<PropertyValue>()

    fun add(property: Property<*>, pane: PanelType = PanelType.PROPERTIES, type: WidgetType = WidgetType.WIDGET) {
        properties.add(PropertyValue(property, pane, type))
    }

    fun add(property: Property<*>, type: WidgetType = WidgetType.WIDGET) {
        properties.add(PropertyValue(property, PanelType.PROPERTIES, type))
    }

    fun get(): MutableList<PropertyValue> {
        return properties
    }
}