package com.greg.model.widgets.properties

import com.greg.model.widgets.properties.extended.ToggleProperty
import javafx.beans.property.ObjectProperty

class Properties {
    private val properties = mutableListOf<PropertyValues>()

    fun get(): MutableList<PropertyValues> {
        return properties
    }

    fun get(property: ToggleProperty): PropertyValues? {
        return properties.firstOrNull { it.property == property }
    }

    fun add(property: ToggleProperty, category: String = "Properties") {
        properties.add(PropertyValues(property, category))
    }

    fun addPanel(property: ToggleProperty, panel: Boolean, category: String = "Properties") {
        properties.add(PanelPropertyValues(property, category, panel))
    }

    fun addCapped(property: ToggleProperty, range: ObjectProperty<IntRange>, category: String = "Properties") : PropertyValues {
        val values = CappedPropertyValues(property, category, range)
        properties.add(values)
        return values
    }
}