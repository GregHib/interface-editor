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

    fun add(property: ToggleProperty, category: String = "Properties") : PropertyValues {
        val values = PropertyValues(property, category)
        properties.add(values)
        return values
    }

    fun addPanel(property: ToggleProperty, panel: Boolean, category: String = "Properties") : PropertyValues {
        val values = PanelPropertyValues(property, category, panel)
        properties.add(values)
        return values
    }

    fun addRanged(property: ToggleProperty, range: ObjectProperty<IntValues>, category: String = "Properties") : PropertyValues {
        val values = RangePropertyValues(property, category, range)
        properties.add(values)
        return values
    }
}